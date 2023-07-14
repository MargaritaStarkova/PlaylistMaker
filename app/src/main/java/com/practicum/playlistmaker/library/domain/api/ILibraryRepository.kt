package com.practicum.playlistmaker.library.domain.api

import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

interface ILibraryRepository {
    fun saveTrack(track: TrackModel)
    fun deleteTrack(track: TrackModel)
    fun getSelectedTracks(): Flow<List<TrackModel>>
    fun isFavorite(id: String): Boolean
}