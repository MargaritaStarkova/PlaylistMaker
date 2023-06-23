package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.FetchResult
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

interface ITrackRepository {
    fun loadTracks(query: String): Flow<FetchResult>
    fun readHistory(): List<TrackModel>
    fun saveHistory(trackList: ArrayList<TrackModel>)
}