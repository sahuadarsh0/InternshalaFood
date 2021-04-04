package com.adarshsahu.internshalafood.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class RegisterActivity : AppCompatActivity() {


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mobile: String
    private lateinit var pass: String
    private lateinit var rePass: String
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var address: String

    private lateinit var etMobile: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etRePassword: EditText
    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var btnSignUp: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        toolbar = findViewById(R.id.toolbar)
        etMobile = findViewById(R.id.etMobile)
        etPassword = findViewById(R.id.etPassword)
        etRePassword = findViewById(R.id.etRePassword)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etAddress = findViewById(R.id.etAddress)
        btnSignUp = findViewById(R.id.btnSignUp)
        progressBar = findViewById(R.id.progressBar)


        toolbar.title = resources.getString(R.string.SignUp)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)//enables the button on the tool bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//displays the icon on the button

        progressBar.visibility = View.INVISIBLE
        btnSignUp.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        mobile = etMobile.text.toString()
        pass = etPassword.text.toString()
        rePass = etRePassword.text.toString()
        name = etName.text.toString()
        email = etEmail.text.toString()
        address = etAddress.text.toString()
        if (mobile.isBlank() || pass.isBlank() || rePass.isBlank() || name.isBlank() || email.isBlank() || address.isBlank()) {
            Toast.makeText(this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show()
        } else if (!isEmailValid(email)) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
        } else if (pass.length < 6) {
            Toast.makeText(this, "Password too Short!", Toast.LENGTH_SHORT).show()
        } else if (name.length < 3) {
            Toast.makeText(this, "Name too Short!", Toast.LENGTH_SHORT).show()
        } else if (pass != rePass) {
            Toast.makeText(this, "Passwords Doesn't Match!", Toast.LENGTH_SHORT).show()
            etPassword.setText("")
            etRePassword.setText("")
        } else if (mobile.length < 10) {
            Toast.makeText(this, "Invalid mobile Number!", Toast.LENGTH_SHORT).show()
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
                register()
            }
        }
    }

    private fun register() {

        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this)
        val userDetails = makeMap().toMap()
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, resources.getString(R.string.api_url) + "register/fetch_result",
            JSONObject(userDetails),
            Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    if (data.getBoolean("success")) {

                        sharedPreferences.edit().putBoolean("user_logged_in", true)
                            .putString("user_id", data.getJSONObject("data").getString("user_id"))
                            .putString("mobile", mobile)
                            .putString("pass", pass)
                            .putString("name", name)
                            .putString("email", email)
                            .putString("address", address)
                            .apply()

                        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
                        dashboardIntent()
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
                Toast.makeText(this, "Error saving Data to the server!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
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

    private fun makeMap(): Map<String, String> {
        val userDetails = HashMap<String, String>()
        userDetails["name"] = name
        userDetails["mobile_number"] = mobile
        userDetails["password"] = pass
        userDetails["address"] = address
        userDetails["email"] = email
        return userDetails
    }


    private fun isEmailValid(email: CharSequence) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun dashboardIntent() {
        startActivity(Intent(this, DashboardActivity::class.java))
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