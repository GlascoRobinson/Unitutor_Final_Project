package com.example.unitutorfinalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var studentIdEditText: EditText
    private lateinit var departmentEditText: EditText
    private lateinit var studentTypeEditText: EditText
    private lateinit var termEditText: EditText
    private lateinit var yearEditText: EditText

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize EditText fields and register button
        nameEditText = findViewById(R.id.nameInput)
        emailEditText = findViewById(R.id.emailInput)
        passwordEditText = findViewById(R.id.passwordInput)
        confirmPasswordEditText = findViewById(R.id.confirmpasswordInput)
        studentIdEditText = findViewById(R.id.studentIdInput)
        departmentEditText = findViewById(R.id.departmentInput)
        studentTypeEditText = findViewById(R.id.studentTypeInput)
        termEditText = findViewById(R.id.termInput)
        yearEditText = findViewById(R.id.yearInput)
        registerButton = findViewById(R.id.submitButton)

        // Set click listener for the register button
        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        // Get values from EditText fields
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val studentId = studentIdEditText.text.toString().trim()
        val department = departmentEditText.text.toString().trim()
        val studentType = studentTypeEditText.text.toString().trim()
        val term = termEditText.text.toString().trim()
        val year = yearEditText.text.toString().trim()

        // Check if any field is empty
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            studentId.isEmpty() || department.isEmpty() || studentType.isEmpty() || term.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if passwords match
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Register user with Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    val user = firebaseAuth.currentUser
                    val userId = user?.uid ?: ""

                    // Store additional user data in Firestore
                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "studentId" to studentId,
                        "department" to department,
                        "studentType" to studentType,
                        "term" to term,
                        "year" to year
                        // Add more fields as needed
                    )

                    // Add user data to Firestore
                    firestore.collection("StoredUsers")
                        .document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error registering user: $e", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Registration failed
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
