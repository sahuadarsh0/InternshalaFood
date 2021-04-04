package com.adarshsahu.internshalafood.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mobile: String
    private lateinit var pass: String

    lateinit var txtSignUp: TextView
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var txtForgotPassword: TextView
    lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        if (sharedPreferences.getBoolean("user_logged_in", false)) {
            dashboardIntent()
        }

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtSignUp = findViewById(R.id.txtSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        progressBar.visibility = View.INVISIBLE


        txtSignUp.setOnClickListener {
            signUpIntent()
        }
        txtForgotPassword.setOnClickListener {
            forgotPasswordIntent()
        }
        btnLogin.setOnClickListener {
            signIn()
        }


    }

    private fun dashboardIntent() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun signUpIntent() = startActivity(Intent(this, RegisterActivity::class.java))

    private fun forgotPasswordIntent() = startActivity(Intent(this, ForgotPassActivity::class.java))


    private fun signIn() {
        mobile = etMobileNumber.text.toString()
        pass = etPassword.text.toString()
        if (mobile.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show()
            etMobileNumber.setText("")
            etPassword.setText("")
        } else if (mobile.length != 10) {
            Toast.makeText(this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show()
            etMobileNumber.setText("")
        } else if (pass.length < 6) {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show()
            etPassword.setText("")
        } else {
            if (!ConnectionManager().checkConnectivity(this)) {
                AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("No Internet")
                    .setMessage("Internet Access has been Restricted.")
                    .setPositiveButton("Open Settings") { _, _ ->
                        startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                    }.show()
            } else {
                login()
            }
        }
    }

    private fun login() {

        progressBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val userDetails = JSONObject()
        userDetails.put("mobile_number", mobile)
        userDetails.put("password", pass)
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, resources.getString(R.string.api_url) + "login/fetch_result",
            userDetails,
            Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    if (data.getBoolean("success")) {
                        val successData = data.getJSONObject("data")


                        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
                        dashboardIntent()

                        sharedPreferences.edit().putBoolean("user_logged_in", true)
                            .putString("user_id", successData.getString("user_id"))
                            .putString("mobile", mobile)
                            .putString("pass", pass)
                            .putString("name", successData.getString("name"))
                            .putString("email", successData.getString("email"))
                            .putString("address", successData.getString("address"))
                            .apply()

                        finish()


                    } else {
                        Toast.makeText(
                            this,
                            "Error: ${data.getString("errorMessage")}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "An Unexpected Error has occurred!", Toast.LENGTH_SHORT)
                        .show()
                }

                progressBar.visibility = View.INVISIBLE
            },
            Response.ErrorListener {

                progressBar.visibility = View.INVISIBLE
                Toast.makeText(this, "Error saving Data to the server!", Toast.LENGTH_SHORT).show()
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

}