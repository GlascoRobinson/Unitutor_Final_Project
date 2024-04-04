package com.example.unitutorfinalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.ContentValues.TAG
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
                    userList.add(DashboardItem(name, email, password, department, studentType, term, year, courseList))
                }
                adapter.itemList = userList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error fetching users", exception)
            }
    }
}
