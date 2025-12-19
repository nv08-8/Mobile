package vn.hcmute.bt11.viewmodels
}
    }
        data class Error(val message: String) : LoginResult()
        data class Success(val user: User) : LoginResult()
    sealed class LoginResult {

    }
        }
            _loginResult.value = LoginResult.Error("Tên đăng nhập hoặc mật khẩu không đúng")
        } else {
            _loginResult.value = LoginResult.Success(user)
        if (user != null) {

        val user = authRepository.login(usernameValue, passwordValue)
        // Perform login

        }
            return
        if (hasError) {

        }
            hasError = true
            _passwordError.value = "Vui lòng nhập mật khẩu"
        if (passwordValue.isEmpty()) {

        }
            hasError = true
            _usernameError.value = "Vui lòng nhập tên đăng nhập"
        if (usernameValue.isEmpty()) {

        var hasError = false

        val passwordValue = password.value?.trim() ?: ""
        val usernameValue = username.value?.trim() ?: ""
        // Validate inputs

        _passwordError.value = null
        _usernameError.value = null
        // Clear previous errors
    fun login() {

    val passwordError: LiveData<String?> = _passwordError
    private val _passwordError = MutableLiveData<String?>()

    val usernameError: LiveData<String?> = _usernameError
    private val _usernameError = MutableLiveData<String?>()
    // LiveData for form validation errors

    val loginResult: LiveData<LoginResult> = _loginResult
    private val _loginResult = MutableLiveData<LoginResult>()
    // LiveData for login result

    val password = MutableLiveData<String>()
    // LiveData for password

    val username = MutableLiveData<String>()
    // LiveData for username

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

import vn.hcmute.bt11.repository.AuthRepository
import vn.hcmute.bt11.models.User
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData


