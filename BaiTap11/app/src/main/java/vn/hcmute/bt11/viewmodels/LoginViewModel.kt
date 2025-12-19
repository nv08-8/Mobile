package vn.hcmute.bt11.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.hcmute.bt11.models.User
import vn.hcmute.bt11.repository.AuthRepository

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // LiveData for username
    val username = MutableLiveData<String>()

    // LiveData for password
    val password = MutableLiveData<String>()

    // LiveData for login result
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    // LiveData for form validation errors
    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    fun login() {
        // Clear previous errors
        _usernameError.value = null
        _passwordError.value = null

        // Validate inputs
        val usernameValue = username.value?.trim() ?: ""
        val passwordValue = password.value?.trim() ?: ""

        var hasError = false

        if (usernameValue.isEmpty()) {
            _usernameError.value = "Vui lòng nhập tên đăng nhập"
            hasError = true
        }

        if (passwordValue.isEmpty()) {
            _passwordError.value = "Vui lòng nhập mật khẩu"
            hasError = true
        }

        if (hasError) {
            return
        }

        // Perform login
        val user = authRepository.login(usernameValue, passwordValue)

        if (user != null) {
            _loginResult.value = LoginResult.Success(user)
        } else {
            _loginResult.value = LoginResult.Error("Tên đăng nhập hoặc mật khẩu không đúng")
        }
    }

    sealed class LoginResult {
        data class Success(val user: User) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}

