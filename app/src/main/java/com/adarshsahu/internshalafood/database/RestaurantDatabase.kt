package com.adarshsahu.internshalafood.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestaurantEntity::class,RestaurantMenuEntity::class],version = 1)
abstract class RestaurantDatabase:RoomDatabase() {
    abstract fun restaurantDao():RestaurantDao

    abstract fun restaurantMenuDao(): RestaurantMenuDao
}

