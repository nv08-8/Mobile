package vn.hcmute.bt11

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import vn.hcmute.bt11.database.DatabaseHelper
import vn.hcmute.bt11.databinding.ActivityRegisterBinding
import vn.hcmute.bt11.repository.AuthRepository
import vn.hcmute.bt11.viewmodels.AuthViewModelFactory
import vn.hcmute.bt11.viewmodels.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this

        // Setup ViewModel
        val databaseHelper = DatabaseHelper(this)
        val authRepository = AuthRepository(databaseHelper)
        val viewModelFactory = AuthViewModelFactory(authRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]

        // Bind ViewModel to layout
        binding.viewModel = viewModel

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        // Observe registration result
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is RegisterViewModel.RegisterResult.Success -> {
                    Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is RegisterViewModel.RegisterResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe validation errors
        viewModel.usernameError.observe(this) { error ->
            binding.tilUsername.error = error
        }

        viewModel.emailError.observe(this) { error ->
            binding.tilEmail.error = error
        }

        viewModel.passwordError.observe(this) { error ->
            binding.tilPassword.error = error
        }

        viewModel.confirmPasswordError.observe(this) { error ->
            binding.tilConfirmPassword.error = error
        }
    }

    private fun setupListeners() {
        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
}

