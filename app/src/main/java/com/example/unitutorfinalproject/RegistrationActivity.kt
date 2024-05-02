package com.example.unitutorfinalproject

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var studentIdEditText: EditText
    private lateinit var departmentEditText: EditText
    private lateinit var studentTypeEditText: EditText
    private lateinit var termEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var GPAEditText: EditText

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize EditText fields and register button
        firstNameEditText = findViewById(R.id.firstNameInput)
        lastNameEditText = findViewById(R.id.lastNameInput)
        emailEditText = findViewById(R.id.emailInput)
        passwordEditText = findViewById(R.id.passwordInput)
        confirmPasswordEditText = findViewById(R.id.confirmpasswordInput)
        studentIdEditText = findViewById(R.id.studentIdInput)
        //departmentEditText = findViewById(R.id.departmentInput)
        //studentTypeEditText = findViewById(R.id.studentTypeInput)
        //termEditText = findViewById(R.id.termInput)
        yearEditText = findViewById(R.id.yearInput)
        GPAEditText = findViewById(R.id.GPAInput)
        registerButton = findViewById(R.id.submitButton)

        formSpinners()

        // Set click listener for the register button
        registerButton.setOnClickListener {
            registerUser()
        }

    }


    private fun formSpinners() {
        // Department spinner
        val spinnerDepartment = findViewById<Spinner>(R.id.departmentSpinner)
        val departmentArray = resources.getStringArray(R.array.department_array)
        val departmentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, departmentArray)
        spinnerDepartment.adapter = departmentAdapter
        // Set a prompt for the spinner
        spinnerDepartment.prompt = "Select Department"

        // Student Type Spinner
        val spinnerStudentType = findViewById<Spinner>(R.id.studentTypeSpinner)
        val studentTypeArray = resources.getStringArray(R.array.student_type_array)
        val studentTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, studentTypeArray)
        spinnerStudentType.adapter = studentTypeAdapter
        // Set a prompt for the spinner
        spinnerStudentType.prompt = "Select Student Type"

        // Term Spinner
        val spinnerTerm = findViewById<Spinner>(R.id.termSpinner)
        val termArray = resources.getStringArray(R.array.term_array)
        val termAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, termArray)
        spinnerTerm.adapter = termAdapter
        // Set a prompt for the spinner
        spinnerTerm.prompt = "Select Term"

        // Other spinners setup goes here...
    }


    private fun registerUser() {
        // Get values from EditText fields
        val firstname = firstNameEditText.text.toString().trim()
        val lastname = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val studentId = studentIdEditText.text.toString().trim()
        // Get values from Spinners
        val department = findViewById<Spinner>(R.id.departmentSpinner).selectedItem.toString()
        val studentType = findViewById<Spinner>(R.id.studentTypeSpinner).selectedItem.toString()
        val term = findViewById<Spinner>(R.id.termSpinner).selectedItem.toString()
        val year = yearEditText.text.toString().trim()
        val GPA = GPAEditText.text.toString().trim()

        // Check if any field is empty
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            studentId.isEmpty() || department.isEmpty() || studentType.isEmpty() || term.isEmpty() || year.isEmpty() || GPA.isEmpty()) {
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
                        "firstname" to firstname,
                        "lastname" to lastname,
                        "email" to email,
                        "studentId" to studentId,
                        "department" to department,
                        "studentType" to studentType,
                        "term" to term,
                        "year" to year,
                        "GPA" to GPA
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
