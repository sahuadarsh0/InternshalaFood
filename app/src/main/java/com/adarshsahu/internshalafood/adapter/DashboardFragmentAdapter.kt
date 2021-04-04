package com.adarshsahu.internshalafood.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.activity.RestaurantDetailsActivity
import com.adarshsahu.internshalafood.database.DBAsyncTask
import com.adarshsahu.internshalafood.database.RestaurantEntity
import com.adarshsahu.internshalafood.model.AllRestaurants
import com.squareup.picasso.Picasso


class DashboardFragmentAdapter(val context: Context, var itemList: ArrayList<AllRestaurants>) :
    RecyclerView.Adapter<DashboardFragmentAdapter.ViewHolderDashboard>() {

    class ViewHolderDashboard(view: View) : RecyclerView.ViewHolder(view) {


        val ivRestaurant: ImageView = view.findViewById(R.id.ivRestaurant)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantId: TextView = view.findViewById(R.id.txtRestaurantId)
        val txtCostForOne: TextView = view.findViewById(R.id.txtCostForOne)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val llRestaurant: LinearLayout = view.findViewById(R.id.llRestaurant)
        val ivFavourite: ImageView = view.findViewById(R.id.ivFavourite)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDashboard {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_restaurant, parent, false)

        return ViewHolderDashboard(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolderDashboard, position: Int) {

        val restaurant = itemList[position]
        val restaurantEntity = RestaurantEntity(
            restaurant.id,
            restaurant.name,
            restaurant.rating.toFloat(),
            restaurant.cost_for_one.toInt(),
            restaurant.image_url,
        )



        holder.txtRestaurantName.text = restaurant.name
        holder.txtRestaurantId.text = restaurant.id.toString()
        holder.txtCostForOne.text = restaurant.cost_for_one + "/Person "
        holder.txtRating.text = restaurant.rating
        Picasso.get().load(restaurant.image_url)
            .error(R.mipmap.ic_launcher_round)
            .into(holder.ivRestaurant)

        holder.ivFavourite.setOnClickListener {
            if (!DBAsyncTask(context.applicationContext, restaurantEntity, 1).execute().get()) {
                val result =
                    DBAsyncTask(context.applicationContext, restaurantEntity, 2).execute().get()
                if (result) {
                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()
                    holder.ivFavourite.setBackgroundResource(R.drawable.ic_favorite)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }

            } else {
                val result =
                    DBAsyncTask(context.applicationContext, restaurantEntity, 3).execute().get()
                if (result) {
                    Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()
                    holder.ivFavourite.setBackgroundResource(R.drawable.ic_unfavorite)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }

            }
        }


        holder.llRestaurant.setOnClickListener {

            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurantId", holder.txtRestaurantId.text.toString())
            intent.putExtra("restaurantName", holder.txtRestaurantName.text.toString())
            context.startActivity(intent)

        }

        val isFav = DBAsyncTask(context, restaurantEntity, 1).execute().get()

        if (isFav) {
            holder.ivFavourite.setBackgroundResource(R.drawable.ic_favorite)
        } else {
            holder.ivFavourite.setBackgroundResource(R.drawable.ic_unfavorite)
        }
    }

    fun filter(filteredList: ArrayList<AllRestaurants>) {
        itemList = filteredList
        notifyDataSetChanged()
    }




}

