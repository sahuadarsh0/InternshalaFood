package com.adarshsahu.internshalafood.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.adapter.DashboardFragmentAdapter
import com.adarshsahu.internshalafood.model.AllRestaurants
import com.adarshsahu.internshalafood.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import java.util.*
import kotlin.collections.HashMap


class DashboardFragment : Fragment() {


    private lateinit var rvDashboard: RecyclerView
    private lateinit var progressBarLayout: RelativeLayout
    private lateinit var noRestaurant: RelativeLayout
    private lateinit var etSearch: EditText
    private var restaurantList = arrayListOf<AllRestaurants>()
    private lateinit var dashboardAdapter: DashboardFragmentAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setHasOptionsMenu(true)

        progressBarLayout = view.findViewById(R.id.progressBarLayout)
        rvDashboard = view.findViewById(R.id.rvDashboard)
        etSearch = view.findViewById(R.id.etSearch)
        noRestaurant = view.findViewById(R.id.noRestaurant)


        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()

        try {
            if (!ConnectionManager().checkConnectivity(activity as Context)) {
                AlertDialog.Builder(activity as Context)
                    .setCancelable(false)
                    .setTitle("No Internet")
                    .setMessage("Internet Access has been Restricted.")
                    .setPositiveButton("Open Settings") { _, _ ->
                        startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                    }.show()
            } else {

                rvDashboard.layoutManager = LinearLayoutManager(activity as Context)

                progressBarLayout.visibility = View.VISIBLE

                val queue = Volley.newRequestQueue(activity as Context)
                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET, resources.getString(R.string.api_url) + "restaurants/fetch_result",

                    null,
                    Response.Listener {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val successData = data.getJSONArray("data")
                            for (i in 0 until successData.length()) {
                                val resultJSONObject = successData.getJSONObject(i)
                                val restaurant = gson.fromJson(resultJSONObject.toString(),
                                    AllRestaurants::class.java)
                                restaurantList.add(restaurant)
                            }
                        }
                        if (activity != null) {
                            dashboardAdapter =
                                DashboardFragmentAdapter(activity as Context, restaurantList)
                            rvDashboard.adapter = dashboardAdapter
                        }
                        progressBarLayout.visibility = View.GONE

                    },
                    Response.ErrorListener {
                        if (activity != null) {
                            Toast.makeText(
                                activity,
                                "Error receiving data from the server!",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressBarLayout.visibility = View.GONE
                        }
                    }) {
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
            if (activity != null)
            Toast.makeText(
                activity as Context,
                "An Unexpected Error has occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        fun filter(strTyped: String) {
            val filteredList = arrayListOf<AllRestaurants>()

            for (item in restaurantList) {
                if (item.name.toLowerCase().contains(strTyped.toLowerCase())) {
                    filteredList.add(item)
                }
            }

            if (filteredList.size == 0) {
                noRestaurant.visibility = View.VISIBLE
            } else {
                noRestaurant.visibility = View.GONE
            }

            dashboardAdapter.filter(filteredList)

        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(strTyped: Editable?) {
                filter(strTyped.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }
        )

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sortRating -> {
                Collections.sort(restaurantList, ratingComparator)
                restaurantList.reverse()
                rvDashboard.adapter?.notifyDataSetChanged()
            }
            R.id.sortCostHighLow -> {
                Collections.sort(restaurantList, priceHighLowComparator)
                restaurantList.reverse()
                rvDashboard.adapter?.notifyDataSetChanged()
            }
            R.id.sortCostLowHigh -> {
                Collections.sort(restaurantList, priceLowHighComparator)
                rvDashboard.adapter?.notifyDataSetChanged()
            }
            else -> {
                item.isChecked = false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private var ratingComparator = Comparator<AllRestaurants> { res1, res2 ->
        if ((res1.rating.compareTo(res2.rating)) == 0) {
            res1.name.compareTo(res2.name) * -1
        } else {
            res1.rating.compareTo(res2.rating)
        }
    }

    private var priceHighLowComparator = Comparator<AllRestaurants> { res1, res2 ->
        if ((res1.cost_for_one.compareTo(res2.cost_for_one)) == 0) {
            res1.name.compareTo(res2.name) * -1
        } else {
            res1.cost_for_one.compareTo(res2.cost_for_one)
        }
    }

    private var priceLowHighComparator = Comparator<AllRestaurants> { res1, res2 ->
        if ((res1.cost_for_one.compareTo(res2.cost_for_one)) == 0) {
            res1.name.compareTo(res2.name)
        } else {
            res1.cost_for_one.compareTo(res2.cost_for_one)
        }
    }


}
