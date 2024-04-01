package com.example.unitutorfinalproject

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var updatePasswordButton: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Get references to the UI elements
        emailEditText = findViewById(R.id.emailEditText)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        updatePasswordButton = findViewById(R.id.updatePasswordButton)

        updatePasswordButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email",
                    Toast.LENGTH_LONG).show()
            } else if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please enter and confirm the new password",
                    Toast.LENGTH_LONG).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, "New password and confirmation do not match",
                    Toast.LENGTH_LONG).show()
            } else {
                updatePassword(email, newPassword)
            }
        }
    }

    private fun updatePassword(email: String, newPassword: String) {
        firebaseAuth.signInWithEmailAndPassword(email, "tempPassword")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.updatePassword(newPassword)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(this, "Password updated successfully",
                                    Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish() // Finish the ForgotPasswordActivity
                            } else {
                                Toast.makeText(this, "Failed to update password",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }
}