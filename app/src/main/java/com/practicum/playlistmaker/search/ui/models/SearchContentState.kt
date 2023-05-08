package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel

sealed interface SearchContentState {
    object Loading : SearchContentState

    data class SearchContent(
        val trackList: List<TrackModel>,
    ) : SearchContentState

    data class HistoryContent(
        val historyList: List<TrackModel>,
    ) : SearchContentState

    data class Error(
        val error: NetworkError,
    ) : SearchContentState
}