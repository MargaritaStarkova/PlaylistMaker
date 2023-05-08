package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.domain.models.NetworkError

interface TrackRepository {
    fun loadTracks(
        query: String,
        onSuccess: (List<TrackModel>) -> Unit,
        onError: (NetworkError) -> Unit,
    )

    fun readHistory(): List<TrackModel>
    fun saveHistory(trackList: ArrayList<TrackModel>)
}