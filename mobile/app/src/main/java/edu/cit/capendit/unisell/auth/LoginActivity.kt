package edu.cit.capendit.unisell.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.cit.capendit.unisell.R
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import edu.cit.capendit.unisell.api.ApiClient
import kotlinx.coroutines.launch
import android.content.Intent
import edu.cit.capendit.unisell.activities.AdminDashboardActivity
import edu.cit.capendit.unisell.activities.VendorDashboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                etEmail.error = if (email.isEmpty()) "Email is required" else null
                etPassword.error = if (password.isEmpty()) "Password is required" else null
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = ApiClient.authApi.login(LoginRequest(email, password))

                    if (response.isSuccessful && response.body() != null) {
                        val authResponse = response.body()!!

                        saveSession(authResponse.id, authResponse.name, authResponse.email, authResponse.role, authResponse.token)

                        val intent = if (authResponse.role == "ADMIN") {
                            Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                        } else {
                            Intent(this@LoginActivity, VendorDashboardActivity::class.java)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login failed: invalid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Network error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun saveSession(id: Long, name: String, email: String, role: String, token: String?) {
        val prefs = getSharedPreferences("UniSellPrefs", MODE_PRIVATE)
        prefs.edit()
            .putLong("user_id", id)
            .putString("user_name", name)
            .putString("user_email", email)
            .putString("user_role", role)
            .apply()
        token?.let { ApiClient.saveToken(it) }
    }
}