package com.practicum.playlistmaker.search.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val iTunesService: ITunesApi,
    private val validator: InternetConnectionValidator,
) : INetworkClient {
    
    override suspend fun doRequest(query: String): Response {
    
        return if (!validator.isConnected()) {
            Response().apply { resultCode = -1 }
    
        } else {
            val response = withContext(Dispatchers.IO) {
                try {
                    iTunesService.search(query)
                } catch (e: Throwable) {
                    null
                }
            }
            val result = response?.body()
            result?.apply { resultCode = response.code() } ?: Response().apply { resultCode = 400 }
        }
    }
}
