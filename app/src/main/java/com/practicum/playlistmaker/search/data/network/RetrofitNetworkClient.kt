package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesService: ITunesApi,
) : INetworkClient {
    
    override fun doRequest(query: String): Response {
        
        return if (!isConnected()) {
            Response().apply { resultCode = -1 }
        } else {
            val response = iTunesService.search(query).execute()
            
            val body = response.body() ?: Response()
            body.apply { resultCode = response.code() }
            
        }
    }
    
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
