package com.adarshsahu.internshalafood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.model.AllOrders
import com.adarshsahu.internshalafood.model.FoodItemDetails
import com.adarshsahu.internshalafood.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


class OrderHistoryAdapter(val context: Context, val orderedRestaurantList: ArrayList<AllOrders>) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolderAllOrders>() {

    class ViewHolderAllOrders(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerViewItemsOrdered: RecyclerView =
            view.findViewById(R.id.rvOrderedItems)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolderAllOrders {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_order, parent, false)

        return ViewHolderAllOrders(view)
    }

    override fun getItemCount(): Int {
        return orderedRestaurantList.size
    }

    override fun onBindViewHolder(holder: ViewHolderAllOrders, position: Int) {
        val restaurantObject = orderedRestaurantList[position]


        holder.txtRestaurantName.text = restaurantObject.restaurant_name
        var formatDate = restaurantObject.order_placed_at
        formatDate = formatDate.replace("-", "/") //  21-02-20 to 21/02/20
        formatDate =
            formatDate.substring(0, 6) + "20" + formatDate.substring(6, 8)//21/02/20 to 21/02/2020
        holder.txtDate.text = formatDate


        var layoutManager = LinearLayoutManager(context)
        var orderedItemAdapter: CartAdapter

        if (ConnectionManager().checkConnectivity(context)) {

            try {

                val orderItemsPerRestaurant = ArrayList<FoodItemDetails>()

                val sharedPreferences =
                    context.getSharedPreferences(context.getString(R.string.shared_preferences),
                        Context.MODE_PRIVATE)

                val user_id = sharedPreferences.getString("user_id", "0")

                val queue = Volley.newRequestQueue(context)

                val url =context.getString(R.string.api_url)+"orders/fetch_result/" + user_id

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            val fetchedRestaurantJsonObject =
                                data.getJSONObject(position)//restaurant at index of position

                            orderItemsPerRestaurant.clear()

                            val foodOrderedJsonArray =
                                fetchedRestaurantJsonObject.getJSONArray("food_items")

                            for (j in 0 until foodOrderedJsonArray.length())//loop through all the items
                            {
                                val eachFoodItem =
                                    foodOrderedJsonArray.getJSONObject(j)//each food item
                                val itemObject = FoodItemDetails(
                                    eachFoodItem.getString("food_item_id"),
                                    eachFoodItem.getString("name"),
                                    eachFoodItem.getString("cost")
                                )

                                orderItemsPerRestaurant.add(itemObject)

                            }

                            orderedItemAdapter = CartAdapter(
                                context,
                                orderItemsPerRestaurant
                            )

                            holder.recyclerViewItemsOrdered.adapter =
                                orderedItemAdapter

                            holder.recyclerViewItemsOrdered.layoutManager =
                                layoutManager


                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"] = "application/json"
                        headers["token"] = context.getString(R.string.token)

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    context,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


    }
}