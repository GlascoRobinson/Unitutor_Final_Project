package com.example.unitutorfinalproject

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DashboardItemsAdapter(
    var itemList: List<DashboardItem>,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : RecyclerView.Adapter<DashboardItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_dashboard_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstnameTextView: TextView = itemView.findViewById(R.id.dashboard_username_textView)
        private val matchUsernameTextView: TextView = itemView.findViewById(R.id.match_username_textView)
        private val matchDepartmentTextView: TextView = itemView.findViewById(R.id.match_department_textView)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.dashboard_progress_bar)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.dashboard_rating_bar)

        fun bind(item: DashboardItem) {
            firstnameTextView.text = item.firstname
            progressBar.max = 100 // Set max progress value
            progressBar.progress = (item.GPA * 25).toInt() // Convert GPA to progress value (0-100)
            ratingBar.rating = item.GPA.toFloat() // Set the rating based on the GPA
            }
        }

        private fun fetchRandomUserInSameDepartment(department: String) {
            firestore.collection("StoredUsers")
                .whereEqualTo("department", department)
                .get()
                .addOnSuccessListener { result ->
                    val userList = result.toObjects(DashboardItem::class.java)
                    if (userList.isNotEmpty()) {
                        val randomUser = userList.random()
                    } else {
                        Log.e("DashboardAdapter", "No users found in the same department: $department")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("DashboardAdapter", "Error fetching users in the same department", exception)
                }
        }
    }

