package com.example.unitutorfinalproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SessionFormActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var courseEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private lateinit var hourlyRateEditText: EditText
    private lateinit var submitButton: Button

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_form)

        // Initialize EditText fields and submit button
        nameEditText = findViewById(R.id.session_form_name)
        courseEditText = findViewById(R.id.session_form_course)
        dateEditText = findViewById(R.id.session_form_date)
        startTimeEditText = findViewById(R.id.session_form_start_time)
        endTimeEditText = findViewById(R.id.session_form_end_time)
        hourlyRateEditText = findViewById(R.id.session_form_hourly_rate)
        submitButton = findViewById(R.id.session_form_submit_btn)

        // Set click listener for the submit button
        submitButton.setOnClickListener {
            saveSessionDataToFirestore()
        }
    }

    private fun saveSessionDataToFirestore() {
        // Get values from EditText fields
        val name = nameEditText.text.toString().trim()
        val sessionCourse = courseEditText.text.toString().trim()
        val sessionDate = dateEditText.text.toString().trim()
        val startTime = startTimeEditText.text.toString().trim()
        val endTime = endTimeEditText.text.toString().trim()
        val hourlyRate = hourlyRateEditText.text.toString().trim()

        // Check if any field is empty
        if (name.isEmpty() || sessionCourse.isEmpty() || sessionDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || hourlyRate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a map to store the session data
        val sessionData = hashMapOf(
            "name" to name,
            "sessionCourse" to sessionCourse,
            "sessionDate" to sessionDate,
            "startTime" to startTime,
            "endTime" to endTime,
            "hourlyRate" to hourlyRate,
            "createdBy" to name
        )

        // Add session data to Firestore
        db.collection("Sessions")
            .add(sessionData)
            .addOnSuccessListener {
                Toast.makeText(this, "Session data added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding session data: $e", Toast.LENGTH_SHORT).show()
            }
    }
}
