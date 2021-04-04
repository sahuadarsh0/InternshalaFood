package com.adarshsahu.internshalafood.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class RestaurantMenuEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val cost: Int,
)