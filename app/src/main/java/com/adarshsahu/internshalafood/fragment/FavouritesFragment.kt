package com.adarshsahu.internshalafood.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.adapter.DashboardFragmentAdapter
import com.adarshsahu.internshalafood.database.FavAsyncTask
import com.adarshsahu.internshalafood.model.AllRestaurants


class FavouritesFragment : Fragment() {


    private lateinit var rvFavourites: RecyclerView
    private var restaurantList = arrayListOf<AllRestaurants>()
    private lateinit var progressBarLayout: RelativeLayout
    private lateinit var tvNoFav: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        rvFavourites = view.findViewById(R.id.rvFavourites)
        progressBarLayout = view.findViewById(R.id.progressBarLayout)
        tvNoFav = view.findViewById(R.id.tvNoFav)

        progressBarLayout.visibility = View.VISIBLE

        val listFromAsync = FavAsyncTask(activity!!.applicationContext).execute().get()
        if (listFromAsync != null  ) {
            listFromAsync.forEach {
                val restaurant = AllRestaurants(it.id,
                    it.name,
                    it.rating.toString(),
                    it.price.toString(),
                    it.image)
                restaurantList.add(restaurant)
            }
            if (listFromAsync.isEmpty()) {
                tvNoFav.visibility = View.VISIBLE
                progressBarLayout.visibility =  View.GONE
            } else {

                rvFavourites.layoutManager = LinearLayoutManager(activity as Context)
                rvFavourites.adapter = DashboardFragmentAdapter(activity as Context, restaurantList)
                progressBarLayout.visibility =  View.GONE
            }
        }


        return view
    }


}