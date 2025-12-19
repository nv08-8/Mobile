package vn.hcmute.bt11.viewmodels
}
    }
        }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
                RegisterViewModel(authRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
            }
                LoginViewModel(authRepository) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
        return when {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")

) : ViewModelProvider.Factory {
    private val authRepository: AuthRepository
class AuthViewModelFactory(

import vn.hcmute.bt11.repository.TaskRepository
import vn.hcmute.bt11.repository.AuthRepository
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel


