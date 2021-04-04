package com.adarshsahu.internshalafood.model

data class AllOrders (
    val order_id:String,
    val restaurant_name:String,
    val total_cost:String,
    val order_placed_at:String,
    val food_items:List<FoodItemDetails>
)

data class FoodItemDetails (

    val food_item_id: String,
    val name: String,
    val cost: String
)
