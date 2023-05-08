package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.domain.models.NetworkError

interface SearchInteractor {

    fun getTracksOnQuery(
        query: String,
        onSuccess: (List<TrackModel>) -> Unit,
        onError: (NetworkError) -> Unit
    )
    fun saveSearchHistory(trackList: ArrayList<TrackModel>)
    fun getTracksFromHistory(): List<TrackModel>

}