package com.example.unitutorfinalproject

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        private val nameTextView: TextView = itemView.findViewById(R.id.dashboard_username_textView)
        private val departmentTextView: TextView = itemView.findViewById(R.id.dashboard_department_textView)
        private val matchUsernameTextView: TextView = itemView.findViewById(R.id.match_username_textView)
        private val matchDepartmentTextView: TextView = itemView.findViewById(R.id.match_department_textView)

        fun bind(item: DashboardItem) {
            nameTextView.text = item.name
            departmentTextView.text = item.department
            item.department?.let { fetchRandomUserInSameDepartment(it) }
        }

        private fun fetchRandomUserInSameDepartment(department: String) {
            firestore.collection("StoredUsers")
                .whereEqualTo("department", department)
                .get()
                .addOnSuccessListener { result ->
                    val userList = result.toObjects(DashboardItem::class.java)
                    if (userList.isNotEmpty()) {
                        val randomUser = userList.random()
                        matchUsernameTextView.text = randomUser.name
                        matchDepartmentTextView.text = randomUser.department
                    } else {
                        Log.e("DashboardAdapter", "No users found in the same department: $department")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("DashboardAdapter", "Error fetching users in the same department", exception)
                }
        }
    }

}
