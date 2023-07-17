package com.practicum.playlistmaker.library.ui.models

import com.practicum.playlistmaker.search.domain.models.TrackModel

sealed interface FavoriteState {
    object Empty : FavoriteState
    
    data class SelectedTracks(
        val trackList: List<TrackModel>,
    ) : FavoriteState
}