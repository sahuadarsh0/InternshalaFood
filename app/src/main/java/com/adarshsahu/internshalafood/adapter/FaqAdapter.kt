package com.adarshsahu.internshalafood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adarshsahu.internshalafood.R

class FaqAdapter(private val menuList: ArrayList<Pair<String, String>>) :
    RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    class FaqViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtQuestion: TextView = view.findViewById(R.id.txtQuestion)
        val txtAnswer: TextView = view.findViewById(R.id.txtAnswer)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.faq_item, parent, false)

        return FaqViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.txtQuestion.text = menuList[position].first
        holder.txtAnswer.text = menuList[position].second
    }
}