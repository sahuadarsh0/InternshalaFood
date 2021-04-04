package com.adarshsahu.internshalafood.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.adapter.RestaurantMenuAdapter
import com.adarshsahu.internshalafood.database.CartAsyncTask
import com.adarshsahu.internshalafood.database.CartItemsAsyncTask
import com.adarshsahu.internshalafood.model.RestaurantMenu
import com.adarshsahu.internshalafood.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder

class RestaurantDetailsActivity : AppCompatActivity() {


    private lateinit var rvRestaurantMenu: RecyclerView

    private lateinit var restaurantId: String
    private lateinit var restaurantName: String
    private lateinit var btnProceedToCart: Button
    private lateinit var progressBarLayout: RelativeLayout

    var menuList = arrayListOf<RestaurantMenu>()
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        toolbar = findViewById(R.id.toolbar)
        progressBarLayout = findViewById(R.id.progressBarLayout)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        rvRestaurantMenu = findViewById(R.id.rvRestaurantMenu)


        restaurantId = intent.getStringExtra("restaurantId").toString()
        restaurantName = intent.getStringExtra("restaurantName").toString()
        toolbar.title = restaurantName

        setSupportActionBar(toolbar)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        CartAsyncTask(this,  mode = 4).execute().get()


        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        btnProceedToCart.visibility = View.GONE
        try {
            if (!ConnectionManager().checkConnectivity(this)) {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("No Internet")
                    .setMessage("Internet Access has been Restricted.")
                    .setPositiveButton("Open Settings") { _, _ ->
                        startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                    }.show()
            } else {
                progressBarLayout.visibility = View.VISIBLE
                val queue = Volley.newRequestQueue(this)
                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET, resources.getString(R.string.api_url) +
                            "restaurants/fetch_result/$restaurantId",
                    null,
                    Response.Listener {
                        val data = it.getJSONObject("data")
                        if (data.getBoolean("success")) {
                            val jsonArray = data.getJSONArray("data")
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val menu = gson.fromJson(jsonObject.toString(),
                                    RestaurantMenu::class.java)
                                menuList.add(menu)
                            }
                            rvRestaurantMenu.layoutManager = LinearLayoutManager(this)
                            rvRestaurantMenu.adapter = RestaurantMenuAdapter(applicationContext,btnProceedToCart, menuList)

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
            progressBarLayout.visibility = View.GONE
        }

        btnProceedToCart.setOnClickListener {
            val cartCount = CartItemsAsyncTask(this).execute().get().size
            if (cartCount == 0) {
                Toast.makeText(this, "Add Food to proceed!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra("restaurantId", restaurantId)
                intent.putExtra("restaurantName", restaurantName)
                startActivity(intent)
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


}