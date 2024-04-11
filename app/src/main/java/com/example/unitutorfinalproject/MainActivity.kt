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

        // Get the logged-in user's email from the intent extras
        val userEmail = intent.getStringExtra("userEmail")
        if (!userEmail.isNullOrEmpty()) {
            fetchUserData(userEmail)
        } else {
            // Handle case where userEmail is null or empty
            Log.e("MainActivity", "Couldn't retrieve account. User email is null or empty")
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

    private fun fetchUserData(userEmail: String) {
        usersCollection.whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { result ->
                val userList = ArrayList<DashboardItem>()
                for (document in result) {
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""
                    val password = document.getString("password") ?: ""
                    val department = document.getString("department") ?: ""
                    val studentType = document.getString("studentType") ?: ""
                    val term = document.getString("term") ?: ""
                    val year = document.getString("year") ?: ""
                    val courseList = document.getString("courseList") ?: ""
                    userList.add(
                        DashboardItem(
                            name,
                            email,
                            password,
                            department,
                            studentType,
                            term,
                            year,
                            courseList
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
