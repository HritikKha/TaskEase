package com.example.taskmanager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateTask : AppCompatActivity() {
    private lateinit var database: myDatabase
    private lateinit var create_title: EditText
    private lateinit var create_priority: EditText
    private lateinit var create_time: EditText
    private lateinit var create_location: EditText
    private lateinit var update_button: Button
    private lateinit var delete_button: Button
    private val channelId="saveNotification"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_task)

        create_title = findViewById(R.id.create_title)
        create_priority = findViewById(R.id.create_priority)
        create_time = findViewById(R.id.create_time)
        create_location = findViewById(R.id.create_location)
        update_button = findViewById(R.id.update_button)
        delete_button = findViewById(R.id.delete_button)

        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "Task_Manger"
        ).build()

        createNotificationChannel()

        val pos = intent.getIntExtra("id", -1)
        if (pos != -1) {
            val title = DataObject.getData(pos).title
            val priority = DataObject.getData(pos).priority
            val time = DataObject.getData(pos).time
            val location = DataObject.getData(pos).location

            create_title.setText(title)
            create_priority.setText(priority)
            create_time.setText(time)
            create_location.setText(location)

            delete_button.setOnClickListener {
                DataObject.deleteData(pos)
                GlobalScope.launch {
                    database.dao().deleteTask(
                        Entity(
                            pos + 1,
                            create_title.text.toString(),
                            create_priority.text.toString(),
                            create_time.text.toString(),
                            create_location.text.toString()
                        )
                    )
                }
                myIntent()
            }

            update_button.setOnClickListener {
                DataObject.updateData(
                    pos,
                    create_title.text.toString(),
                    create_priority.text.toString(),
                    create_time.text.toString(),
                    create_location.text.toString()
                )
                GlobalScope.launch {
                    database.dao().updateTask(
                        Entity(
                            pos + 1,
                            create_title.text.toString(),
                            create_priority.text.toString(),
                            create_time.text.toString(),
                            create_location.text.toString()
                        )
                    )
                }
                showNotification("Task Updated!", "Task has been updated")
                myIntent()
            }
        }
    }

    fun myIntent() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Guest Notifications"
            val descriptionText = "Notifications for guest updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}
