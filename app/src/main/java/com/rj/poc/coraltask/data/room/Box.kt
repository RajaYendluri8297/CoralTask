package com.rj.poc.coraltask.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boxes")
data class Box(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
)