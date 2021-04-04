package com.adarshsahu.internshalafood.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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

class ForgotPassActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var mobile: String
    private lateinit var etMobile: EditText
    private lateinit var etEmail: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var btnNext: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        email = ""
        mobile = ""
        btnNext = findViewById(R.id.btnNext)
        etMobile = findViewById(R.id.etMobile)
        etEmail = findViewById(R.id.etEmail)
        progressBar = findViewById(R.id.progressBar)
        toolbar = findViewById(R.id.toolbar)

        toolbar.title = resources.getString(R.string.forgot_password)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)//enables the button on the tool bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//displays the icon on the button

        progressBar.visibility = View.GONE

        btnNext.setOnClickListener {
            email = etEmail.text.toString()
            mobile = etMobile.text.toString()
            if (email.isBlank() || mobile.isBlank()) {
                Toast.makeText(this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show()
                etMobile.setText("")
                etEmail.setText("")
            } else if (!isEmailValid(email)) {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            } else if (mobile.length < 10) {
                Toast.makeText(this, "Invalid Phone Number!", Toast.LENGTH_SHORT).show()
            } else {
                progressBar.visibility = View.VISIBLE
                forgotPass()
            }
        }

    }

    private fun forgotPass() {


        val queue = Volley.newRequestQueue(this)
        val userDetails = JSONObject()
        userDetails.put("mobile_number", mobile)
        userDetails.put("email", email)
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, resources.getString(R.string.api_url) + "forgot_password/fetch_result",
            userDetails,
            Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    if (data.getBoolean("success")) {
                        if (!data.getBoolean("first_try"))
                            Toast.makeText(this, "Use same OTP for 24 Hours", Toast.LENGTH_LONG)
                                .show()
                        otpIntent()
                    } else {
                        progressBar.visibility = View.GONE

                        Toast.makeText(
                            this,
                            "Error: ${data.getString("errorMessage")}",
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

    private fun isEmailValid(email: CharSequence) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun otpIntent() {
        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra("mobile", mobile)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if(item.itemId ==android.R.id.home){
            onBackPressed();
        }
        return true
    }
}