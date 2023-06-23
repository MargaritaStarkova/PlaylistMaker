package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

interface ISearchInteractor {
    val historyList: ArrayList<TrackModel>
    fun getTracksOnQuery(query: String): Flow<Pair<List<TrackModel>?, NetworkError?>>
    fun addTrackToHistoryList(track: TrackModel)
    fun historyListCleared()
}