package com.adarshsahu.internshalafood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.model.FoodItemDetails

class CartAdapter(val context: Context, val cartItemsList: ArrayList<FoodItemDetails>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvCartItemName: TextView = view.findViewById(R.id.tvCartItemName)
        val tvCartItemPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            LayoutInflater.from(context).inflate(R.layout.single_cart, parent, false)
        )

    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        holder.tvCartItemName.text = cartItemsList[position].name
        holder.tvCartItemPrice.text = ("Rs. ${cartItemsList[position].cost}")
    }

    override fun getItemCount(): Int {
        return cartItemsList.size
    }

}

