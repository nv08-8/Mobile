package vn.hcmute.bt10_kotlin
}
    }
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    private fun navigateToMain() {

    }
        }
            Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
        } else {
            navigateToMain()
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

            }
                apply()
                putString("username", user.username)
                putInt("userId", user.id)
            with(sharedPref.edit()) {
            val sharedPref = getSharedPreferences("TodoListPrefs", Context.MODE_PRIVATE)
            // Save user session
        if (user != null) {
        val user = databaseHelper.loginUser(username, password)
    private fun loginUser(username: String, password: String) {

    }
        return true
        }
            return false
            binding.etPassword.error = "Vui lòng nhập mật khẩu"
        if (password.isEmpty()) {
        }
            return false
            binding.etUsername.error = "Vui lòng nhập tên đăng nhập"
        if (username.isEmpty()) {
    private fun validateInput(username: String, password: String): Boolean {

    }
        }
            startActivity(Intent(this, RegisterActivity::class.java))
        binding.tvRegister.setOnClickListener {

        }
            }
                loginUser(username, password)
            if (validateInput(username, password)) {

            val password = binding.etPassword.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
        binding.btnLogin.setOnClickListener {
    private fun setupListeners() {

    }
        setupListeners()

        }
            return
            navigateToMain()
        if (userId != -1) {
        val userId = sharedPref.getInt("userId", -1)
        val sharedPref = getSharedPreferences("TodoListPrefs", Context.MODE_PRIVATE)
        // Check if user is already logged in

        databaseHelper = DatabaseHelper(this)

        setContentView(binding.root)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    override fun onCreate(savedInstanceState: Bundle?) {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var binding: ActivityLoginBinding
class LoginActivity : AppCompatActivity() {

import vn.hcmute.bt10_kotlin.databinding.ActivityLoginBinding
import vn.hcmute.bt10_kotlin.database.DatabaseHelper
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.os.Bundle
import android.content.Intent
import android.content.Context


