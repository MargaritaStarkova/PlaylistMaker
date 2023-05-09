package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.models.TrackModel

sealed interface HistoryListState {
    data class ItemInserted(
        val itemPosition: Int,
        val positionStart: Int,
        val historyList: List<TrackModel>,
    ) : HistoryListState
    
    data class ItemRemoved(
        val itemPosition: Int,
        val positionStart: Int,
        val historyList: List<TrackModel>,
    ) : HistoryListState
}