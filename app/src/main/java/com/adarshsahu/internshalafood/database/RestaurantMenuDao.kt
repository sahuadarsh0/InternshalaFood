package com.adarshsahu.internshalafood.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantMenuDao {

    @Insert
    fun insertItemToCart(restaurantMenuEntity: RestaurantMenuEntity)

    @Delete
    fun removeItemFromCart(restaurantMenuEntity: RestaurantMenuEntity)

    @Query("SELECT * FROM cart")
    fun getCartContents(): List<RestaurantMenuEntity>

    @Query("SELECT SUM(cost) FROM cart")
    fun getCartTotal(): Int

    @Query("DELETE FROM cart")
    fun deleteAllCartItems()

    @Query("SELECT * FROM cart WHERE id=:resId")
    fun checkIfAddedToCart(resId: String): RestaurantMenuEntity
}