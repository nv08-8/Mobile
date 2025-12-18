package vn.hcmute.bt10_kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.hcmute.bt10_kotlin.database.DatabaseHelper
import vn.hcmute.bt10_kotlin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        // Check if user is already logged in
        val sharedPref = getSharedPreferences("TodoListPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)
        if (userId != -1) {
            navigateToMain()
            return
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(username, password)) {
                loginUser(username, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            binding.etUsername.error = "Vui lòng nhập tên đăng nhập"
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Vui lòng nhập mật khẩu"
            return false
        }
        return true
    }

    private fun loginUser(username: String, password: String) {
        val user = databaseHelper.loginUser(username, password)
        if (user != null) {
            // Save user session
            val sharedPref = getSharedPreferences("TodoListPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putInt("userId", user.id)
                putString("username", user.username)
                apply()
            }

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
            navigateToMain()
        } else {
            Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

