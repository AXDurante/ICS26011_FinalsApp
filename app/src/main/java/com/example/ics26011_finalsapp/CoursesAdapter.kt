package com.example.ics26011_finalsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CoursesAdapter(
    private val courseList: ArrayList<Courses>,
    private val onItemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<CoursesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_courses, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = courseList[position]
        holder.courseName.text = currentItem.course_name
        holder.courseDesc.text = currentItem.course_desc

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClickListener.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: TextView = itemView.findViewById(R.id.courseName)
        val courseDesc: TextView = itemView.findViewById(R.id.courseDesc)
    }
}