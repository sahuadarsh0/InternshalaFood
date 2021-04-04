package com.adarshsahu.internshalafood.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.database.CartAsyncTask
import com.adarshsahu.internshalafood.database.CartItemsAsyncTask
import com.adarshsahu.internshalafood.database.RestaurantMenuEntity
import com.adarshsahu.internshalafood.model.RestaurantMenu


class RestaurantMenuAdapter(
    private val context: Context,
    private val btnProceedToCart: Button,
    private val menuList: ArrayList<RestaurantMenu>,

    ) : RecyclerView.Adapter<RestaurantMenuAdapter.RestaurantMenuViewHolder>() {

    class RestaurantMenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtNumber: TextView = view.findViewById(R.id.txtNumber)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val buttonAddToCart: Button = view.findViewById(R.id.buttonAddToCart)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {

        return RestaurantMenuViewHolder(
            LayoutInflater.from(context).inflate(R.layout.single_menu, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, position: Int) {

//      holder.txtNumber.text = menuList[position].id
        holder.txtNumber.text = ("${position + 1}.")
        holder.txtItemName.text = menuList[position].name
        holder.txtItemPrice.text = ("Rs.${menuList[position].cost_for_one}")
        holder.buttonAddToCart.text = context.getText(R.string.add)
        holder.buttonAddToCart.setOnClickListener {
            if (CartAsyncTask(context, id = menuList[position].id, mode = 3).execute()
                    .get()
            ) {
                //remove from cart
                val result = CartAsyncTask(
                    context,
                    RestaurantMenuEntity(
                        menuList[position].id,
                        menuList[position].name,
                        menuList[position].cost_for_one.toInt()
                    ),
                    mode = 2
                ).execute().get()
                if (result) {
                    holder.buttonAddToCart.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                    holder.buttonAddToCart.text = context.getString(R.string.add)
                } else {
                    Toast.makeText(context, "An Error Has Occurred!", Toast.LENGTH_SHORT).show()
                }

            } else {
                //add to cart
                val result = CartAsyncTask(
                    context,
                    RestaurantMenuEntity(
                        menuList[position].id,
                        menuList[position].name,
                        menuList[position].cost_for_one.toInt()
                    ),
                    mode = 1
                ).execute().get()
                if (result) {
                    holder.buttonAddToCart.setBackgroundColor(context.resources.getColor(R.color.colorAccent))
                    holder.buttonAddToCart.text = context.getString(R.string.remove)
                } else {
                    Toast.makeText(context, "An Error Has Occurred!", Toast.LENGTH_SHORT).show()
                }
            }
            if (CartItemsAsyncTask(context).execute().get().isNotEmpty()) {
                btnProceedToCart.visibility = View.VISIBLE
            } else {
                btnProceedToCart.visibility = View.GONE
            }
            Log.d("TAG", "onBindViewHolder:${CartItemsAsyncTask(context).execute().get()} ")
        }

    }
}
