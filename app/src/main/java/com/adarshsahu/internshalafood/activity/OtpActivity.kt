package com.adarshsahu.internshalafood.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.adarshsahu.internshalafood.R
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class OtpActivity : AppCompatActivity() {


    private lateinit var progressBar: ProgressBar
    private lateinit var btnSubmit: Button
    private lateinit var toolbar: Toolbar
    private lateinit var mobile: String
    private lateinit var otp: String
    private lateinit var pass: String
    private lateinit var etPassword: EditText
    private lateinit var etRePassword: EditText
    private lateinit var etOtp: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)


        otp = ""
        mobile = ""
        pass = ""
        btnSubmit = findViewById(R.id.btnSubmit)
        etPassword = findViewById(R.id.etPassword)
        etRePassword = findViewById(R.id.etRePassword)
        etOtp = findViewById(R.id.etOtp)

        mobile = intent.getStringExtra("mobile")!!

        progressBar = findViewById(R.id.progressBar)
        toolbar = findViewById(R.id.toolbar)

        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)//enables the button on the tool bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//displays the icon on the button

        progressBar.visibility = View.GONE

        btnSubmit.setOnClickListener {
            otp = etOtp.text.toString()
            pass = etPassword.text.toString()
            val rePass = etRePassword.text.toString()
            when {
                otp.length < 4 -> {
                    Toast.makeText(this, "Invalid OTP.", Toast.LENGTH_SHORT).show()
                }
                pass != rePass -> {
                    Toast.makeText(this, "Passwords Don't Match!", Toast.LENGTH_SHORT).show()
                    etPassword.setText("")
                    etRePassword.setText("")
                }
                pass.length < 6 -> {
                    Toast.makeText(
                        this,
                        "Password should be at least 6 characters.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    progressBar.visibility = View.VISIBLE

                    checkOtp()
                }
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


    private fun checkOtp() {
        val queue = Volley.newRequestQueue(this)
        val userDetails = JSONObject()
        userDetails.put("mobile_number", mobile)
        userDetails.put("password", pass)
        userDetails.put("otp", otp)

        Log.d("TAG", "checkOtp: $userDetails")
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, resources.getString(R.string.api_url) +
                    "reset_password/fetch_result",
            userDetails,
            Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    if (data.getBoolean("success")) {

                        Toast.makeText(
                            this,
                            "Password changed successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        loginIntent()
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            data.getString("errorMessage"),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } catch (e: Exception) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "An Unexpected Error has occurred!", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            Response.ErrorListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Error sending Data to the server!", Toast.LENGTH_SHORT).show()
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

    private fun loginIntent() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}