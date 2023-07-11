package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.FetchResult
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

interface ISearchInteractor {
    val historyList: ArrayList<TrackModel>
    fun getTracksOnQuery(query: String): Flow<FetchResult>
    fun addTrackToHistoryList(track: TrackModel)
    fun historyListCleared()
}