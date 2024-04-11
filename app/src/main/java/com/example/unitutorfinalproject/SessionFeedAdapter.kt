package com.example.unitutorfinalproject
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class SessionFeedAdapter(var sessionList: List<Session>) :
    RecyclerView.Adapter<SessionFeedAdapter.SessionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_feed_item, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessionList[position]
        holder.bind(session)
    }

    override fun getItemCount(): Int = sessionList.size

    inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.session_name_textView)
        private val courseTextView: TextView = itemView.findViewById(R.id.session_course_textView)
        private val dateTextView: TextView = itemView.findViewById(R.id.session_feed_date_textView)
        private val startTimeTextView: TextView = itemView.findViewById(R.id.session_feed_starttime_textView)
        private val endTimeTextView: TextView = itemView.findViewById(R.id.session_feed_endtime_textView)
        private val rateTextView: TextView = itemView.findViewById(R.id.session_feed_rate_textView)

        fun bind(session: Session) {
            nameTextView.text = session.name
            courseTextView.text = session.sessionCourse
            dateTextView.text = session.sessionDate
            startTimeTextView.text = session.startTime
            endTimeTextView.text = session.endTime
            rateTextView.text = session.hourlyRate
        }
    }
}
