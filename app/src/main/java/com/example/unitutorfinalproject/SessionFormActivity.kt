package com.example.unitutorfinalproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SessionFormActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var courseAutoCompleteTextView: AutoCompleteTextView
    private lateinit var dateButton: Button
    private lateinit var startTimeButton: Button
    private lateinit var endTimeButton: Button
    private lateinit var submitButton: Button
    private lateinit var hourlyRateSeekBar: SeekBar
    private lateinit var hourlyRateDisplay: TextView

    // Define hourly rates array
    private val hourlyRates = arrayOf(500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500)

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_form)

        // Initialize EditText fields and submit button
        firstNameEditText = findViewById(R.id.session_form_firstname)
        lastNameEditText = findViewById(R.id.session_form_lastname)
        courseAutoCompleteTextView = findViewById(R.id.session_form_course_autocomplete)
        dateButton = findViewById(R.id.session_form_date_button)
        startTimeButton = findViewById(R.id.session_form_start_time_button)
        endTimeButton = findViewById(R.id.session_form_end_time_button)
        hourlyRateSeekBar = findViewById(R.id.session_form_hourly_rate_seekbar)
        hourlyRateDisplay = findViewById(R.id.session_form_hourly_rate_display)
        submitButton = findViewById(R.id.session_form_submit_btn)


        // Set up the course autocomplete
        setupCourseAutoComplete()

        // Set click listener for the date Button
        dateButton.setOnClickListener {
            showDatePicker()
        }

        // Set click listener for the start time Button
        startTimeButton.setOnClickListener {
            showTimePicker(startTimeButton)
        }

        // Set click listener for the end time Button
        endTimeButton.setOnClickListener {
            showTimePicker(endTimeButton)
        }


        // Set up SeekBar listener
        hourlyRateSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update hourly rate display based on progress
                val selectedHourlyRate = hourlyRates[progress]
                hourlyRateDisplay.text = "Hourly Rate: $$selectedHourlyRate"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }
        })

        // Set initial hourly rate display
        val initialHourlyRate = hourlyRates[hourlyRateSeekBar.progress]
        hourlyRateDisplay.text = "Hourly Rate: $$initialHourlyRate"



    // Set click listener for the submit button
        submitButton.setOnClickListener {
            saveSessionDataToFirestore()
        }
    }

    private fun setupCourseAutoComplete() {
        // Access the array of courses from resources
        val courses = resources.getStringArray(R.array.courses)

        // Create an ArrayAdapter with the course list
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, courses)

        // Set the adapter to the AutoCompleteTextView
        courseAutoCompleteTextView.setAdapter(adapter)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val formattedDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                dateButton.text = formattedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(button: Button) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedTime.set(Calendar.MINUTE, selectedMinute)
                val formattedTime =
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selectedTime.time)
                button.text = formattedTime
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }


    private fun saveSessionDataToFirestore() {
        // Get values from EditText fields
        val firstname = firstNameEditText.text.toString().trim()
        val lastname = lastNameEditText.text.toString().trim()
        val sessionCourse = courseAutoCompleteTextView.text.toString().trim()
        val sessionDate = dateButton.text.toString().trim()
        val startTime = startTimeButton.text.toString().trim()
        val endTime = endTimeButton.text.toString().trim()
        val hourlyRate = hourlyRates[hourlyRateSeekBar.progress].toString()

        // Check if any field is empty
        if (firstname.isEmpty() || lastname.isEmpty() || sessionCourse.isEmpty() || sessionDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || hourlyRate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a map to store the session data
        val sessionData = hashMapOf(
            "firstname" to firstname,
            "lastname" to lastname,
            "sessionCourse" to sessionCourse,
            "sessionDate" to sessionDate,
            "startTime" to startTime,
            "endTime" to endTime,
            "hourlyRate" to hourlyRate,
            "createdBy" to firstname
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