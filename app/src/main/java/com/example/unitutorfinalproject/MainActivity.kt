package com.example.unitutorfinalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DashboardItemsAdapter
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("StoredUsers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.dashboard_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DashboardItemsAdapter(ArrayList())
        recyclerView.adapter = adapter

        // Set up bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // No need to navigate, already on home
                    true
                }
                R.id.navigation_exchange -> {
                    startActivity(Intent(this, TutorshipActivity::class.java))
                    true
                }
                R.id.navigation_find -> {
                    startActivity(Intent(this, FindActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Get the logged-in user's email from the intent extras
        val userEmail = intent.getStringExtra("userEmail")
        if (!userEmail.isNullOrEmpty()) {
            fetchUserData(userEmail)
        } else {
            // Handle case where userEmail is null or empty
            Log.e("MainActivity", "Couldn't retrieve account. User email is null or empty")
        }
    }

    private fun fetchUserData(userEmail: String) {
        usersCollection.whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { result ->
                val userList = ArrayList<DashboardItem>()
                for (document in result) {
                    val firstname = document.getString("firstname") ?: ""
                    val GPA = document.getDouble("GPA") ?: 0.0 // Default value if GPA is null
                    userList.add(
                        DashboardItem(
                            firstname,
                            GPA
                        )
                    )
                }
                adapter.itemList = userList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error fetching users", exception)
            }
    }
}
