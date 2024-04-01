package com.example.unitutorfinalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    private val db =Firebase.firestore
    private val onlineUserDatabase = db.collection("StoredUsers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, WelcomeScreen::class.java))

        /*/ Set click listeners for buttons
        userLoginButton.setOnClickListener {
            // Start LoginActivity when loginButton is clicked
            startActivity(Intent(this, LoginActivity::class.java))
        }

        userRegistrationButton.setOnClickListener {
            // Start RegistrationActivity when registrationButton is clicked
            startActivity(Intent(this, RegistrationActivity::class.java))
        }*/

        // Additional initialization or functionality can be added here
    }
}
