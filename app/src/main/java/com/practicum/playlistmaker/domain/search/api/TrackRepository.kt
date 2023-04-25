package com.practicum.playlistmaker.domain.search.api

import com.practicum.playlistmaker.domain.models.TrackModel
import com.practicum.playlistmaker.domain.models.NetworkError

interface TrackRepository {
    fun loadTracks(
        query: String,
        onSuccess: (List<TrackModel>) -> Unit,
        onError: (NetworkError) -> Unit,
    )

    fun readHistory(): List<TrackModel>
    fun saveHistory(trackList: ArrayList<TrackModel>)
}