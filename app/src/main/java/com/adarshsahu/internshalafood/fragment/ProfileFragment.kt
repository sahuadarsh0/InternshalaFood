package com.adarshsahu.internshalafood.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.adarshsahu.internshalafood.R


class ProfileFragment : Fragment() {


    private lateinit var sharedPreferences: SharedPreferences
    lateinit var txtName: TextView
    lateinit var txtEmail: TextView
    lateinit var txtMobileNumber: TextView
    lateinit var txtAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtName = view.findViewById(R.id.txtName)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber)
        txtAddress = view.findViewById(R.id.txtAddress)

        sharedPreferences =
            context?.applicationContext!!.getSharedPreferences(getString(R.string.shared_preferences),
                Context.MODE_PRIVATE)

        txtName.text = sharedPreferences.getString("name", "")
        txtEmail.text = sharedPreferences.getString("email", "")
        txtMobileNumber.text = "+91 " + sharedPreferences.getString("mobile", "")
        txtAddress.text = sharedPreferences.getString("address", "")


        return view
    }


}