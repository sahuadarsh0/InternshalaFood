package com.adarshsahu.internshalafood.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import com.adarshsahu.internshalafood.R

class OrderPlacedActivity : AppCompatActivity() {

    lateinit var btnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        btnOk=findViewById(R.id.btnOk)

        btnOk.setOnClickListener(View.OnClickListener {

            val intent= Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finishAffinity()//finish all the activities
        })
    }


    override fun onBackPressed() {

        val intent= Intent(this,DashboardActivity::class.java)
        startActivity(intent)
        finishAffinity()//finish all the activities
    }
}