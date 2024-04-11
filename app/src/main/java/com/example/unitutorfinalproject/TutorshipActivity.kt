package com.example.unitutorfinalproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.ContentValues.TAG
import android.content.Intent
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class TutorshipActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SessionFeedAdapter // Declare RecyclerView adapter

    private lateinit var requestButton: Button
    private lateinit var offerButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorship_exchange)

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.session_feed_recyclerView)
        adapter = SessionFeedAdapter(ArrayList()) // Initialize the adapter with an empty list

        // Get references to the UI elements
                requestButton = findViewById(R.id.request_session_btn)
        offerButton = findViewById(R.id.offer_session_btn)


        // Set layout manager and adapter to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Fetch session data from Firestore
        fetchSessionData()

        // REQUEST button onClickListener
        requestButton.setOnClickListener {
            // Authenticate user and navigate to the appropriate activity
            startActivity(Intent(this, SessionFormActivity::class.java))

        }

        // REQUEST button onClickListener
        offerButton.setOnClickListener {
            // Authenticate user and navigate to the appropriate activity
            startActivity(Intent(this, SessionFormActivity::class.java))
        }

        // Set up bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // Navigate to activity_main.xml
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.navigation_exchange -> {
                    // Do nothing as already on the tutorship activity
                    true
                }
                R.id.navigation_find -> {
                    // Navigate to activity_find.xml
                    startActivity(Intent(this, FindActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchSessionData() {
        // Implement logic to fetch session data from your database collection
        // Example: Use Firebase Firestore to fetch session data
        val db = FirebaseFirestore.getInstance()
        val sessionCollection = db.collection("Sessions")

        sessionCollection.get()
            .addOnSuccessListener { result ->
                val sessionList = ArrayList<Session>()

                for (document in result) {
                    val name = document.getString("name") ?: ""
                    val sessionCourse = document.getString("sessionCourse") ?: ""
                    val sessionDate = document.getString("sessionDate") ?: ""
                    val startTime = document.getString("startTime") ?: ""
                    val endTime = document.getString("endTime") ?: ""
                    val hourlyRate = document.getString("hourlyRate") ?: ""

                    val session = Session(name, sessionCourse, sessionDate, startTime, endTime, hourlyRate)
                    sessionList.add(session)
                }

                // Update the adapter with the fetched session data
                adapter.sessionList = sessionList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching session data", exception)
            }
    }
}
