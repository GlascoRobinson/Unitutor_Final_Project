package com.example.unitutorfinalproject


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FindAdapter(var findList: List<Find>) :
    RecyclerView.Adapter<FindAdapter.FindViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_find_item, parent, false)
        return FindViewHolder(view)
    }

    override fun onBindViewHolder(holder: FindViewHolder, position: Int) {
        val find = findList[position]
        holder.bind(find)
    }

    override fun getItemCount(): Int = findList.size

    class FindViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstnameTextView: TextView = itemView.findViewById(R.id.find_firstname_textView)
        private val lastnameTextView: TextView = itemView.findViewById(R.id.find_lastname_textView)
        private val emailTextView: TextView = itemView.findViewById(R.id.find_email_textView)
        private val studentTypeTextView: TextView = itemView.findViewById(R.id.find_studentType_textView)
        private val departmentTextView: TextView = itemView.findViewById(R.id.find_department_textView)

        fun bind(find: Find) {
            firstnameTextView.text = find.firstname
            lastnameTextView.text = find.lastname
            emailTextView.text = find.email
            studentTypeTextView.text = find.studentType
            departmentTextView.text = find.department
        }
    }
}
