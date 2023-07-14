package com.practicum.playlistmaker.library.domain.api

import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

interface ILibraryInteractor {
    fun likeTrack(track: TrackModel)
    fun unLikeTrack(track: TrackModel)
    fun getSelectedTracks(): Flow<List<TrackModel>>
    fun isFavorite(id: String): Boolean
}