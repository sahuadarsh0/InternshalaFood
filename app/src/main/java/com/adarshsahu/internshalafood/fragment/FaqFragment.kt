package com.adarshsahu.internshalafood.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.adapter.FaqAdapter

class FaqFragment : Fragment() {


    private var menuList = ArrayList<Pair<String, String>>()
    private lateinit var rvFaq: RecyclerView
    private lateinit var progressBarLayout: RelativeLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val view = inflater.inflate(R.layout.fragment_faq, container, false)

        rvFaq = view.findViewById(R.id.recyclerFaq)
        progressBarLayout = view.findViewById(R.id.progressBarLayout)
        initValues()

        rvFaq.layoutManager = LinearLayoutManager(activity as Context)
        rvFaq.adapter = FaqAdapter(menuList)
        progressBarLayout.visibility = View.GONE
        rvFaq.adapter!!.notifyDataSetChanged()
        return view
    }

    private fun initValues() {
        menuList.add(
            Pair(
                "Q: What is food runner App?",
                "A: Food Runner is a food delivery service that does Home Deliveries to it's customers, from over 200 restaurants / outlets in Pune, Pimpri-Chinchwad and Navi-Mumbai."
            )
        )
        menuList.add(
            Pair(
                "Q: When Can I Order?",
                "A: Our services are available 24x7x365."
            )
        )
        menuList.add(
            Pair(
                "Q: Can I order from 2 or more restaurants at the same time?",
                "A: No."
            )
        )
        menuList.add(
            Pair(
                "Q: How long can Refunds take?",
                "A: A refund normally takes place between 0-3 bank days."
            )
        )
        menuList.add(
            Pair(
                "Q: I gave the wrong Order, Can I change the items?",
                "A: No, you have to cancel the order, and order again."
            )
        )
        menuList.add(
            Pair(
                "Q: How to go on particular restaurant page?",
                "A: There are multiple ways to go on particular restaurant page. Search restaurant city wise or cuisine wise and select restaurant from list. "
            )
        )
        menuList.add(
            Pair(
                "Q: What payment modes are available?",
                "A: We have Cash,Debit Card,PayTM and UPI options."
            )
        )
    }

}
