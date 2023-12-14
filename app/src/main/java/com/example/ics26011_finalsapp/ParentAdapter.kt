package com.example.ics26011_finalsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ParentAdapter(
    private val parentList: List<ParentItem>,
    private val userId: Long,
    private val getChildItems: (Long) -> List<ChildItem>, // Pass user ID to ChildAdapter
    private val onAddActivityClick: (Long, Long) -> Unit // Callback function
) : RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_course_weight, parent, false)
        return ParentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val currentItem = parentList[position]
        holder.nameAssessment.text = currentItem.assessmentName
        holder.weightNum.text = currentItem.gradeWeight.toString()

        // Get child items associated with the parent item using the lambda function
        val childItems = getChildItems(currentItem.assessmentId.toLong())

        // Set up ChildAdapter for the child RecyclerView
        val childAdapter = ChildAdapter(childItems)
        holder.childRecyclerView.layoutManager = LinearLayoutManager(holder.childRecyclerView.context)
        holder.childRecyclerView.adapter = childAdapter
        childAdapter.updateItems(childItems)
    }

    override fun getItemCount(): Int {
        return parentList.size
    }

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameAssessment: TextView = itemView.findViewById(R.id.nameAssessment)
        val weightNum: TextView = itemView.findViewById(R.id.weightNum)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.childRecyclerView)
        val createActivity: Button = itemView.findViewById(R.id.button)

        init {
            createActivity.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAddActivityClick(
                        parentList[position].assessmentId.toLong(),
                        userId
                    )
                }
            }

            // Assuming you have a function to get child items for a parent (assessment)
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val childItems = getChildItems(parentList[position].assessmentId.toLong())

                // Set up ChildAdapter for the child RecyclerView
                val childAdapter = ChildAdapter(childItems)
                childRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
                childRecyclerView.adapter = childAdapter
            }
        }
    }
}