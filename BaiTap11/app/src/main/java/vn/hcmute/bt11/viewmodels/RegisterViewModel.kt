package vn.hcmute.bt11.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.hcmute.bt11.repository.AuthRepository

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // LiveData for form fields
    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    // LiveData for registration result
    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    // LiveData for form validation errors
    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError

    fun register() {
        // Clear previous errors
        _usernameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null

        // Get values
        val usernameValue = username.value?.trim() ?: ""
        val emailValue = email.value?.trim() ?: ""
        val passwordValue = password.value?.trim() ?: ""
        val confirmPasswordValue = confirmPassword.value?.trim() ?: ""

        var hasError = false

        // Validate username
        if (usernameValue.isEmpty()) {
            _usernameError.value = "Vui lòng nhập tên đăng nhập"
            hasError = true
        } else if (usernameValue.length < 3) {
            _usernameError.value = "Tên đăng nhập phải có ít nhất 3 ký tự"
            hasError = true
        } else if (authRepository.isUsernameExists(usernameValue)) {
            _usernameError.value = "Tên đăng nhập đã tồn tại"
            hasError = true
        }

        // Validate email
        if (emailValue.isEmpty()) {
            _emailError.value = "Vui lòng nhập email"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Email không hợp lệ"
            hasError = true
        } else if (authRepository.isEmailExists(emailValue)) {
            _emailError.value = "Email đã được sử dụng"
            hasError = true
        }

        // Validate password
        if (passwordValue.isEmpty()) {
            _passwordError.value = "Vui lòng nhập mật khẩu"
            hasError = true
        } else if (passwordValue.length < 6) {
            _passwordError.value = "Mật khẩu phải có ít nhất 6 ký tự"
            hasError = true
        }

        // Validate confirm password
        if (confirmPasswordValue.isEmpty()) {
            _confirmPasswordError.value = "Vui lòng xác nhận mật khẩu"
            hasError = true
        } else if (passwordValue != confirmPasswordValue) {
            _confirmPasswordError.value = "Mật khẩu xác nhận không khớp"
            hasError = true
        }

        if (hasError) {
            return
        }

        // Perform registration
        val result = authRepository.register(usernameValue, emailValue, passwordValue)

        if (result != -1L) {
            _registerResult.value = RegisterResult.Success
        } else {
            _registerResult.value = RegisterResult.Error("Đăng ký thất bại. Vui lòng thử lại")
        }
    }

    sealed class RegisterResult {
        object Success : RegisterResult()
        data class Error(val message: String) : RegisterResult()
    }
}

