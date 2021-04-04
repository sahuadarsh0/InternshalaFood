package com.adarshsahu.internshalafood.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.adapter.OrderHistoryAdapter
import com.adarshsahu.internshalafood.model.AllOrders
import com.adarshsahu.internshalafood.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder


class OrderHistoryFragment : Fragment() {


    private lateinit var rvOrder: RecyclerView
    private var ordersList = arrayListOf<AllOrders>()
    private lateinit var progressBarLayout: RelativeLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvNoOrders: TextView
    private lateinit var txtBelow: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        rvOrder = view.findViewById(R.id.rvOrder)
        progressBarLayout = view.findViewById(R.id.progressBarLayout)
        tvNoOrders = view.findViewById(R.id.tvNoOrders)
        txtBelow = view.findViewById(R.id.txtBelow)


        sharedPreferences =
            context?.applicationContext!!.getSharedPreferences(getString(R.string.shared_preferences),
                Context.MODE_PRIVATE)


        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()

        progressBarLayout.visibility = View.VISIBLE


        try {
            if (!ConnectionManager().checkConnectivity(activity as Context)) {
                androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                    .setCancelable(false)
                    .setTitle("No Internet")
                    .setMessage("Internet Access has been Restricted.")
                    .setPositiveButton("Open Settings") { _, _ ->
                        startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                    }.show()
            } else {
                progressBarLayout.visibility = View.VISIBLE

                rvOrder.layoutManager = LinearLayoutManager(activity as Context)
                val queue = Volley.newRequestQueue(activity as Context)
                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    resources.getString(R.string.api_url)
                            + "orders/fetch_result/"
                            + sharedPreferences.getString("user_id", "0"),
                    null,
                    Response.Listener {
                        val data = it.getJSONObject("data")
                        if (data.getBoolean("success")) {
                            val resultJSONArray = data.getJSONArray("data")
                            if (resultJSONArray.length() == 0) {
                                tvNoOrders.visibility = View.VISIBLE
                                txtBelow.visibility = View.GONE

                            } else {
                                txtBelow.visibility = View.VISIBLE
                                tvNoOrders.visibility = View.GONE

                                for (i in 0 until resultJSONArray.length()) {
                                    val resultJSONObject = resultJSONArray.getJSONObject(i)
                                    val order = gson.fromJson(resultJSONObject.toString(),
                                        AllOrders::class.java)
                                    ordersList.add(order)
                                }


                                if (activity != null) {
                                    rvOrder.adapter = OrderHistoryAdapter(
                                        activity as Context,
                                        ordersList
                                    )
                                }
                            }
                            progressBarLayout.visibility = View.GONE


                        }
                    },
                    Response.ErrorListener {
                        progressBarLayout.visibility = View.GONE

                        Toast.makeText(
                            activity as Context,
                            "Error receiving data from the server!",
                            Toast.LENGTH_SHORT
                        ).show()
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
            Toast.makeText(activity as Context,
                "An Unexpected Error has occurred!",
                Toast.LENGTH_SHORT).show()
            progressBarLayout.visibility = View.GONE
        }
        return view
    }


    private fun sortList() {
    }
}