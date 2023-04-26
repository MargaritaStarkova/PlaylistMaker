package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.storage.models.TrackModelDto
import com.practicum.playlistmaker.domain.models.NetworkError

interface NetworkClient {
    fun doRequest(
        query: String,
        onSuccess: (List<TrackModelDto>) -> Unit,
        onError: (NetworkError) -> Unit,
    )
}