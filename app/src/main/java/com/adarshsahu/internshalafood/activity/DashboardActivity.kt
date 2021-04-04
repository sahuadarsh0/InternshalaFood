package com.adarshsahu.internshalafood.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.adarshsahu.internshalafood.R
import com.adarshsahu.internshalafood.fragment.*
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var frameLayout: FrameLayout
    private lateinit var navView: NavigationView
    private lateinit var txtCurrentUser: TextView
    private lateinit var txtMobileNumber: TextView
    var previousMenuItemSelected: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        frameLayout = findViewById(R.id.frameLayout)
        navView = findViewById(R.id.navView)

        val headerView = navView.getHeaderView(0)

        txtCurrentUser = headerView.findViewById(R.id.txtCurrentUser)
        txtMobileNumber = headerView.findViewById(R.id.txtMobileNumber)
        txtCurrentUser.text = sharedPreferences.getString("name", "")
        txtMobileNumber.text = sharedPreferences.getString("mobile", "")


        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)//enables the button on the tool bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//displays the icon on the button

        openDashboard()

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        navView.setNavigationItemSelectedListener {


            if (previousMenuItemSelected != null) {
                previousMenuItemSelected?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItemSelected = it



            drawerLayout.closeDrawer(GravityCompat.START)
            when (it.itemId) {

                R.id.home -> {
                    openDashboard()
                }
                R.id.myProfile -> {
                    toolbar.title = "My Profile"
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            ProfileFragment()
                        )
                        .commit()
                }
                R.id.favouriteRestaurants -> {
                    toolbar.title = "Favorites"
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FavouritesFragment()
                        )
                        .commit()
                }
                R.id.orderHistory -> {

                    toolbar.title = "Order History"
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            OrderHistoryFragment()
                        )
                        .commit()
                }
                R.id.faqs -> {
                    toolbar.title = "FAQ"
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FaqFragment()
                        )
                        .commit()

                }
//
                R.id.logout -> {
                    AlertDialog
                        .Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure You want to log out?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            sharedPreferences.edit().clear().apply()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finishAffinity()
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onBackPressed() {

        when (supportFragmentManager.findFragmentById(R.id.frameLayout)) {
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()
        }
    }

    private fun openDashboard() {
        supportActionBar?.title = "All Restaurants"
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frameLayout,
                DashboardFragment()
            )
            .commit()

        navView.setCheckedItem(R.id.home)
    }
}