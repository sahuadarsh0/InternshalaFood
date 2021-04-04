package com.adarshsahu.internshalafood.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo


 class ConnectionManager {

      fun checkConnectivity(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        return if (activeNetworkInfo?.isConnected != null) {
            activeNetworkInfo.isConnected
        } else {
            false
        }

    }
}