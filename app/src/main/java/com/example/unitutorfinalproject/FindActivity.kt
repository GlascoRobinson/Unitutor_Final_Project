package com.example.unitutorfinalproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.ContentValues.TAG
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class FindActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FindAdapter // Declare RecyclerView adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.find_recyclerView)
        adapter = FindAdapter(ArrayList()) // Initialize the adapter with an empty list

        // Set layout manager and adapter to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Fetch find data from Firestore
        fetchFindData()

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
                    // Navigate to activity_tutorship.xml
                    startActivity(Intent(this, TutorshipActivity::class.java))
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

    private fun fetchFindData() {
        // Implement logic to fetch find user data from your database collection
        // Example: Use Firebase Firestore to fetch find user data
        val db = FirebaseFirestore.getInstance()
        val findCollection = db.collection("StoredUsers")

        findCollection.get()
            .addOnSuccessListener { result ->
                val findList = ArrayList<Find>()

                for (document in result) {
                    val firstname = document.getString("firstname") ?: ""
                    val lastname = document.getString("lastname") ?: ""
                    val email = document.getString("email") ?: ""
                    val department = document.getString("department") ?: ""
                    val studentType = document.getString("studentType") ?: ""

                    val find = Find(firstname, lastname, email, department, studentType)
                    findList.add(find)
                }

                // Update the adapter with the fetched find user data
                adapter.findList = findList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching find user data", exception)
            }
    }

}
