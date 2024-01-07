package com.example.bookshelf.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast

fun getConnMgr(context: Context, service: (ser: String) -> Any): ConnectivityManager {

    val connMgr = service(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            showSnackBar("Strong connection found!", context)
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            showSnackBar("Connection failing !", context)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            showSnackBar("poor network", context)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            showSnackBar("Network unavailable", context)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
        }
    }



    connMgr.requestNetwork(networkRequest, networkCallback)
    return connMgr

}

fun showSnackBar(message: String, context: Context) {
    val toast = Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    )
    toast.show()

}

object NetworkStatus {

    fun isConnected(connMgr: ConnectivityManager): Boolean {
        return connMgr.activeNetworkInfo!!.isConnected
    }
}


