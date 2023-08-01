package com.practicum.playlistmaker.search.data.network

class RetrofitNetworkClient(
    private val iTunesService: ITunesApi,
    private val validator: InternetConnectionValidator,
) : NetworkClient {
    
    override suspend fun doRequest(query: String): Response {
    
        return if (!validator.isConnected()) {
            Response().apply { resultCode = -1 }
    
        } else {
            val response = try {
                iTunesService.search(query)
            } catch (e: Throwable) {
                null
            }
    
            val result = response?.body()
            result?.apply { resultCode = response.code() } ?: Response().apply { resultCode = 400 }
        }
    }
}
