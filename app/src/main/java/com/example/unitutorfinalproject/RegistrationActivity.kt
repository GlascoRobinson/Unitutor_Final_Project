package com.example.unitutorfinalproject

import android.content.Intent
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import java.io.File
import java.io.FileOutputStream
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

/*import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper*/




class RegistrationActivity : AppCompatActivity() {

    private lateinit var photoUploadButton: Button
    private lateinit var transcriptUploadButton: Button
    private lateinit var registerButton: Button
    private lateinit var addCourseButton: Button
    private lateinit var courseContainer: LinearLayout

    private lateinit var nameInput: TextInputEditText
    private lateinit var studentTypeInput: TextInputEditText
    private lateinit var termInput: TextInputEditText
    private lateinit var yearInput: TextInputEditText
    private lateinit var studentIdInput: TextInputEditText

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private val firebaseStorage = FirebaseStorage.getInstance().reference

    private var transcriptUri: Uri? = null
    private var photoUri: Uri? = null

    private val courseList = mutableListOf<String>()

    private val REQUEST_TRANSCRIPT_CODE = 101
    private val REQUEST_PHOTO_CODE = 102

    private val gradingSystem = mapOf(
        "A" to 10, "A-" to 9, "B+" to 8, "B" to 7, "B-" to 6,
        "C+" to 5, "C" to 4, "C-" to 3, "D" to 2, "F" to 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        photoUploadButton = findViewById(R.id.photoUploadButton)
        transcriptUploadButton = findViewById(R.id.transcriptUploadButton)
        registerButton = findViewById(R.id.submitButton)
        addCourseButton = findViewById(R.id.addCourseButton)
        courseContainer = findViewById(R.id.courseContainer)

        nameInput = findViewById(R.id.nameInput)
        studentTypeInput = findViewById(R.id.studentTypeInput)
        termInput = findViewById(R.id.termInput)
        yearInput = findViewById(R.id.yearInput)
        studentIdInput = findViewById(R.id.studentIdInput)

        photoUploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PHOTO_CODE)
        }

        transcriptUploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, REQUEST_TRANSCRIPT_CODE)
        }

        addCourseButton.setOnClickListener {
            addCourseInput()
        }

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun addCourseInput() {
        val courseInputLayout = TextInputLayout(this)
        courseInputLayout.hint = "Course (e.g., CPTR322: Mobile Application Development)"
        courseInputLayout.setPadding(0, 20, 0, 0)

        val courseInput = TextInputEditText(this)
        // Set any specific input types or filters here if needed
        courseInputLayout.addView(courseInput)

        courseContainer.addView(courseInputLayout)

        // Add the course to the courseList
        val course = courseInput.text.toString().trim()
        if (course.isNotEmpty()) {
            courseList.add(course)
        }
    }

    private fun registerUser() {
        val name = nameInput.text.toString().trim()
        val studentType = studentTypeInput.text.toString().trim()
        val term = termInput.text.toString().trim()
        val year = yearInput.text.toString().trim()
        val studentId = studentIdInput.text.toString().trim()

        if (name.isEmpty() || studentType.isEmpty() || term.isEmpty() || year.isEmpty() || studentId.isEmpty() || courseList.isEmpty() || transcriptUri == null || photoUri == null) {
            Toast.makeText(this, "Please fill in all fields and upload required documents", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword("$studentId@example.com", "password123")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val userId = user?.uid ?: ""

                    val userInfo = hashMapOf(
                        "name" to name,
                        "studentType" to studentType,
                        "term" to term,
                        "year" to year,
                        "studentId" to studentId,
                        "courses" to courseList
                    )

                    // Calculate user's performance based on transcript
                    val performance = calculatePerformanceFromTranscript(transcriptUri)
                    userInfo["performance"] = performance

                    firebaseDatabase.child("users").child(userId).setValue(userInfo)
                        .addOnCompleteListener { databaseTask ->
                            if (databaseTask.isSuccessful) {
                                uploadTranscript(userId)
                                uploadPhoto(userId)
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                // Navigate to the appropriate activity or do any other necessary actions
                            } else {
                                Toast.makeText(this, "Registration failed: ${databaseTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun calculatePerformanceFromTranscript(transcriptUri: Uri?): Map<String, Int> {
        val performance = mutableMapOf<String, Int>()
        transcriptUri?.let { uri ->
            try {
                // Convert PDF to text file
                val tempFile = File(cacheDir, "transcript.txt")
                convertPdfToTextFile(uri, tempFile)

                // Read the text content from the file
                val textContent = readTextFromFile(tempFile.absolutePath)
                val lines = textContent.split("\n")

                // Assuming the format of each line is "Course Code - Grade"
                for (line in lines) {
                    val parts = line.split(" - ")
                    if (parts.size == 2) {
                        val courseCode = parts[0].trim()
                        val grade = parts[1].trim()

                        // Calculate performance based on grade
                        val performanceValue = when (grade) {
                            in gradingSystem.keys -> gradingSystem[grade] ?: 0
                            else -> 0 // Default value if grade is not recognized
                        }

                        performance[courseCode] = performanceValue
                    }
                }

                // Delete the temporary file
                tempFile.delete()
            } catch (e: Exception) {
                Toast.makeText(this, "Error reading transcript: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        return performance
    }

    private fun convertPdfToTextFile(pdfUri: Uri, outputFile: File) {
        val inputStream = contentResolver.openInputStream(pdfUri)
        val pdfDocument = PDDocument.load(inputStream)
        val pdfStripper = PDFTextStripper()
        val pdfText = pdfStripper.getText(pdfDocument)
        pdfDocument.close()

        val outputStream = FileOutputStream(outputFile)
        outputStream.write(pdfText.toByteArray())
        outputStream.close()
    }

    private fun readTextFromFile(filePath: String): String {
        return File(filePath).readText()
    }

    private fun uploadTranscript(userId: String) {
        transcriptUri?.let {
            val transcriptRef = firebaseStorage.child("transcripts/$userId.pdf")
            transcriptRef.putFile(it)
                .addOnSuccessListener {
                    // Transcript upload successful
                }
                .addOnFailureListener {
                    // Transcript upload failed
                    Toast.makeText(this, "Transcript upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadPhoto(userId: String) {
        photoUri?.let {
            val photoRef = firebaseStorage.child("photos/$userId.jpg")
            photoRef.putFile(it)
                .addOnSuccessListener {
                    // Photo upload successful
                }
                .addOnFailureListener {
                    // Photo upload failed
                    Toast.makeText(this, "Photo upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_TRANSCRIPT_CODE -> {
                    transcriptUri = data.data
                    // Handle transcript selection
                }
                REQUEST_PHOTO_CODE -> {
                    photoUri = data.data
                    // Handle photo selection
                }
            }
        }
    }
}

