package com.example.dreamweaver.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dreams")
data class DreamEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val mood: String,
    val isLucid: Boolean,
    val timestamp: Long
)
