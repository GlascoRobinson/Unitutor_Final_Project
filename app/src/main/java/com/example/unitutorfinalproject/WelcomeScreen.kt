package com.example.unitutorfinalproject


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeScreen : AppCompatActivity() {

    private lateinit var userLoginButton:Button
    private lateinit var userRegistrationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_screen)

        userLoginButton = findViewById(R.id.loginButton)
        userRegistrationButton = findViewById(R.id.registrationButton)

        // Set click listeners for buttons
        userLoginButton.setOnClickListener {
            // Start LoginActivity when loginButton is clicked
            startActivity(Intent(this, LoginActivity::class.java))
        }

        userRegistrationButton.setOnClickListener {
            // Start RegistrationActivity when registrationButton is clicked
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        // Additional initialization or functionality can be added here
    }
}
