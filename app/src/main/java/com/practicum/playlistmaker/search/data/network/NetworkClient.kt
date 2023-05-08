package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.storage.models.TrackModelDto
import com.practicum.playlistmaker.search.domain.models.NetworkError

interface NetworkClient {
    fun doRequest(
        query: String,
        onSuccess: (List<TrackModelDto>) -> Unit,
        onError: (NetworkError) -> Unit,
    )
}