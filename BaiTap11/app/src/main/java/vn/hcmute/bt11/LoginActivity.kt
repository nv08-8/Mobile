package vn.hcmute.bt11

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import vn.hcmute.bt11.database.DatabaseHelper
import vn.hcmute.bt11.databinding.ActivityLoginBinding
import vn.hcmute.bt11.repository.AuthRepository
import vn.hcmute.bt11.viewmodels.AuthViewModelFactory
import vn.hcmute.bt11.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this

        // Check if user is already logged in
        val sharedPref = getSharedPreferences("TodoListPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)
        if (userId != -1) {
            navigateToMain()
            return
        }

        // Setup ViewModel
        val databaseHelper = DatabaseHelper(this)
        val authRepository = AuthRepository(databaseHelper)
        val viewModelFactory = AuthViewModelFactory(authRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        // Bind ViewModel to layout
        binding.viewModel = viewModel

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        // Observe login result
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginViewModel.LoginResult.Success -> {
                    // Save user session
                    val sharedPref = getSharedPreferences("TodoListPrefs", MODE_PRIVATE)
                    sharedPref.edit().apply {
                        putInt("userId", result.user.id)
                        putString("username", result.user.username)
                        apply()
                    }

                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is LoginViewModel.LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe validation errors
        viewModel.usernameError.observe(this) { error ->
            binding.tilUsername.error = error
        }

        viewModel.passwordError.observe(this) { error ->
            binding.tilPassword.error = error
        }
    }

    private fun setupListeners() {
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
