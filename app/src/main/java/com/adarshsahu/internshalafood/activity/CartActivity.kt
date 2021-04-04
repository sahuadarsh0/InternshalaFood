package com.adarshsahu.internshalafood.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.adapter.CartAdapter
import com.adarshsahu.internshalafood.database.CartAsyncTask
import com.adarshsahu.internshalafood.database.CartItemsAsyncTask
import com.adarshsahu.internshalafood.database.CartTotalAsyncTask
import com.adarshsahu.internshalafood.model.FoodItemDetails
import com.adarshsahu.internshalafood.model.RestaurantMenu
import com.adarshsahu.internshalafood.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {


    private lateinit var rvCartMenu: RecyclerView

    private lateinit var restaurantId: String
    private lateinit var restaurantName: String
    private lateinit var btnPlaceOrder: Button
    private lateinit var progressBarLayout: RelativeLayout
    private lateinit var txtOrderingFrom: TextView
    private lateinit var sharedPreferences: SharedPreferences

    var menuList = arrayListOf<RestaurantMenu>()
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar = findViewById(R.id.toolbar)
        progressBarLayout = findViewById(R.id.progressBarLayout)
        txtOrderingFrom = findViewById(R.id.txtOrderingFrom)
        rvCartMenu = findViewById(R.id.rvCartMenu)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)


        restaurantId = intent.getStringExtra("restaurantId").toString()
        restaurantName = intent.getStringExtra("restaurantName").toString()
        toolbar.title = "My Cart"
        txtOrderingFrom.text = restaurantName

        val total = CartTotalAsyncTask(this).execute().get()
        btnPlaceOrder.text = resources.getString(R.string.place_order) + " ( Total : Rs.$total )"

        setSupportActionBar(toolbar)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


//        val builder = GsonBuilder()
//        builder.setPrettyPrinting()
//        val gson = builder.create()

        rvCartMenu.layoutManager = LinearLayoutManager(this)


        val foodJsonArray = JSONArray()

        val dbCartItemsList = CartItemsAsyncTask(this).execute().get()
        val cartItemsList = arrayListOf<FoodItemDetails>()
        for (i in dbCartItemsList.indices) {
            cartItemsList.add(
                FoodItemDetails(
                    dbCartItemsList[i].id,
                    dbCartItemsList[i].name,
                    dbCartItemsList[i].cost.toString()
                )
            )
            val tempJSONObject = JSONObject()
            tempJSONObject.put("food_item_id", dbCartItemsList[i].id)
            foodJsonArray.put(tempJSONObject)
        }
        Log.d("TAG", "onCreate: $foodJsonArray ")

        rvCartMenu.adapter = CartAdapter(this, cartItemsList)

        btnPlaceOrder.setOnClickListener {
            try {
                if (!ConnectionManager().checkConnectivity(this)) {
                    AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("No Internet")
                        .setMessage("Internet Access has been Restricted.")
                        .setPositiveButton("Open Settings") { _, _ ->
                            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                        }.show()
                } else {

                    progressBarLayout.visibility = View.VISIBLE

                    val queue = Volley.newRequestQueue(this)
                    val params = JSONObject()
                    params.put("user_id", sharedPreferences.getString("user_id", "0"))
                    params.put("restaurant_id", restaurantId)
                    params.put("total_cost", total)
                    params.put("food", foodJsonArray)
                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST,
                        resources.getString(R.string.api_url) + "place_order/fetch_result/",
                        params,
                        Response.Listener {
                            val data = it.getJSONObject("data")
                            if (data.getBoolean("success")) {
                                orderPlacedIntent()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Error retrieving Data from the server!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            progressBarLayout.visibility = View.GONE
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this,
                                "Error retrieving Data from the internet!",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressBarLayout.visibility = View.GONE
                        }
                    ) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = getString(R.string.token)
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)


                }
            } catch (e: Exception) {
                Toast.makeText(this, "An Unexpected Error has occurred!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == android.R.id.home) {
            onBackPressed();
        }
        return true
    }


    private fun orderPlacedIntent() {
        CartAsyncTask(this, mode = 4).execute().get()
        startActivity(Intent(this, OrderPlacedActivity::class.java))
        finishAffinity()
    }
}