package com.example.taskmanager

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
class MainActivity : AppCompatActivity() {
    private lateinit var database: myDatabase  // Corrected database name
    private lateinit var add: FloatingActionButton
    private lateinit var deleteAll: ImageButton
    private lateinit var recycler_view: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        deleteAll = findViewById(R.id.deleteAll)
        add = findViewById(R.id.add)
        recycler_view = findViewById(R.id.recycler_view)

        // Initialize the database
        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "Task_Manager"
        )
            .fallbackToDestructiveMigration()  // Automatically resets the database if schema changes
            .build()

        add.setOnClickListener {
            val intent = Intent(this, CreateTask::class.java)
            startActivity(intent)
        }

        deleteAll.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure you want to delete all tasks?")
                .setPositiveButton("Yes") { dialog, which ->
                    DataObject.deleteAll()
                    GlobalScope.launch {
                        database.dao().deleteAll()
                    }
                    setRecycler()
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
            builder.create().show()
        }

        setRecycler()  // Set up RecyclerView when activity starts
    }

    private fun setRecycler() {
        lifecycleScope.launch {
            // Refresh the data in RecyclerView after deletion
            val data = DataObject.getAllData() // This should return fresh data from the database
            recycler_view.adapter = Adapter(data)
            recycler_view.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }
}
