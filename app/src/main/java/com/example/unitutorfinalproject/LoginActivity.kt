package com.example.unitutorfinalproject

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var forgotPasswordButton: Button
    private lateinit var loginButton: Button
    private lateinit var registrationButton: Button
    private lateinit var userEmail: TextInputEditText
    private lateinit var userPassword: TextInputEditText
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Get references to the UI elements
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton)
        loginButton = findViewById(R.id.loginButton)
        registrationButton = findViewById(R.id.registrationButton)
        userEmail = findViewById(R.id.emailInput)
        userPassword = findViewById(R.id.passwordInput)

        loginButton.setOnClickListener {
            // Authenticate user and navigate to the appropriate activity
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()
            validateUser(email, password)

        }


        // Set click listeners for buttons
        forgotPasswordButton.setOnClickListener {
            // Handle forgot password functionality
            userForgotPassword()
        }


        registrationButton.setOnClickListener {
            // Start RegistrationActivity
            newUserRegister()
        }
    }

    private fun validateUser(email: String, password: String) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password",
                Toast.LENGTH_LONG).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("userEmail", email)
                    startActivity(intent)
                    finish() // Optional: Finish the LoginActivity to prevent going back
                } else {
                    // Authentication failed, display an error message
                    Toast.makeText(this, "Authentication failed: " +
                            "${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun userForgotPassword(){
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

    private fun newUserRegister()
    {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }
}