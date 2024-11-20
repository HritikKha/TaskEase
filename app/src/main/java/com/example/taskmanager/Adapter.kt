package com.example.taskmanager

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(var data: List<CardInfo>) : RecyclerView.Adapter<Adapter.viewHolder>() {

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val priority: TextView = itemView.findViewById(R.id.priority)
        val time: TextView = itemView.findViewById(R.id.time) // New TextView for time
        val location: TextView = itemView.findViewById(R.id.location) // New TextView for location
        val layout: LinearLayout = itemView.findViewById(R.id.mylayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view, parent, false)
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentTask = data[position]

        // Set background color based on priority
        when (currentTask.priority.toLowerCase()) {
            "high" -> holder.layout.setBackgroundColor(Color.parseColor("#E57373")) // Crimson Red
            "medium" -> holder.layout.setBackgroundColor(Color.parseColor("#FFD54F")) // Amber Yellow
            "low" -> holder.layout.setBackgroundColor(Color.parseColor("#81D4FA")) // Mint Green
            "complete" -> holder.layout.setBackgroundColor(Color.parseColor("#B0BEC5")) // Sky Blue
            else -> holder.layout.setBackgroundColor(Color.WHITE)
        }

        // Set values for title, priority, time, and location
        holder.title.text = currentTask.title
        holder.priority.text = currentTask.priority
        holder.time.text = currentTask.time // Set time
        holder.location.text = currentTask.location // Set location

        // Set onClickListener to open UpdateTask screen
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateTask::class.java)
            intent.putExtra("id", position)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
