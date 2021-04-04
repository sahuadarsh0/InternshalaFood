package com.adarshsahu.internshalafood.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val rating: Float,
    val price: Int,
    val image: String
)