package com.practicum.playlistmaker.search.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val iTunesService: ITunesApi,
    private val validator: InternetConnectionValidator,
) : INetworkClient {
    
    override suspend fun doRequest(query: String): Response {
    
        if (!validator.isConnected()) {
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
}
