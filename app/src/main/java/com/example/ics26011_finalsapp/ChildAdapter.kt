package com.example.ics26011_finalsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChildAdapter(
    private var childList: List<ChildItem>
) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.child_activities, parent, false)
        return ChildViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val currentItem = childList[position]
        holder.activityName.text = currentItem.activityName
        holder.userScore.text = "User Score: ${currentItem.userScore}"
        holder.maxScore.text = "Max Score: ${currentItem.maxScore}"
        
    }

    override fun getItemCount(): Int {
        return childList.size
    }

    class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityName: TextView = itemView.findViewById(R.id.activityName)
        val userScore: TextView = itemView.findViewById(R.id.userScore)
        val maxScore: TextView = itemView.findViewById(R.id.maxScore)
    }

    fun updateItems(newItems: List<ChildItem>) {
        childList = newItems
        notifyDataSetChanged()
    }
}