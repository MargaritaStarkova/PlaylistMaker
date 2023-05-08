package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.storage.models.TrackModelDto
import com.practicum.playlistmaker.search.domain.models.NetworkError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkClientImpl(
    private val api: SearchApi
) : NetworkClient {

    override fun doRequest(
        query: String,
        onSuccess: (List<TrackModelDto>) -> Unit,
        onError: (NetworkError) -> Unit
    ) {
        api.search(query).enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>, response: Response<SearchResponse>
                ) {
                    when (response.code()) {
                        in 400..499 -> {
                            onError.invoke(NetworkError.SEARCH_ERROR)
                        }

                        in 500..599 -> onError.invoke(NetworkError.CONNECTION_ERROR)
                        else -> {
                            val result = response.body()?.results ?: emptyList()
                            onSuccess.invoke(result.filter { it.previewUrl != null })
                        }
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    onError.invoke(NetworkError.CONNECTION_ERROR)
                }
            })
    }
}
