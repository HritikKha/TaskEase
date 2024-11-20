package com.example.taskmanager

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "Task_Manager")
data class Entity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: String,
    var time: String,
    var location: String
)
