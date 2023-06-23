package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesService: ITunesApi,
) : INetworkClient {
    
    override suspend fun doRequest(query: String): Response {
    
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        
        } else {
            val response = withContext(Dispatchers.IO) {
                try {
                    iTunesService.search(query)
                } catch (e: Throwable) {
                    null
                }
            }
        
            return if (response?.body() != null) {
                when (response.code()) {
                    in 400..499 -> Response().apply { resultCode = 400 }
                    
                    in 500..599 -> {
                        Response().apply { resultCode = 500 }
                    }
                    
                    else -> {
                        response.body()?.apply { resultCode = 200 }!!
                    }
                }
            } else {
                Response().apply {
                    resultCode = 400
                }
            }
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
