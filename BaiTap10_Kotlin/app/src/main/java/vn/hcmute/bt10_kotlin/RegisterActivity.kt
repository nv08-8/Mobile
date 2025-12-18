package vn.hcmute.bt10_kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.hcmute.bt10_kotlin.database.DatabaseHelper
import vn.hcmute.bt10_kotlin.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInput(username, email, password, confirmPassword)) {
                registerUser(username, email, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (username.isEmpty()) {
            binding.etUsername.error = "Vui lòng nhập tên đăng nhập"
            return false
        }
        if (username.length < 3) {
            binding.etUsername.error = "Tên đăng nhập phải có ít nhất 3 ký tự"
            return false
        }
        if (email.isEmpty()) {
            binding.etEmail.error = "Vui lòng nhập email"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Email không hợp lệ"
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Vui lòng nhập mật khẩu"
            return false
        }
        if (password.length < 6) {
            binding.etPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
            return false
        }
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Mật khẩu không khớp"
            return false
        }
        return true
    }

    private fun registerUser(username: String, email: String, password: String) {
        if (databaseHelper.isUsernameExists(username)) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show()
            return
        }

        val result = databaseHelper.registerUser(username, email, password)
        if (result != -1L) {
            Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Đăng ký thất bại. Vui lòng thử lại", Toast.LENGTH_SHORT).show()
        }
    }
}

