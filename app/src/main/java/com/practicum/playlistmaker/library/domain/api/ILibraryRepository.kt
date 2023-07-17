package com.practicum.playlistmaker.library.domain.api

import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

interface ILibraryRepository {
    suspend fun saveTrack(track: TrackModel)
    suspend fun deleteTrack(track: TrackModel)
    fun getSelectedTracks(): Flow<List<TrackModel>>
    fun isFavorite(id: String): Flow<Boolean>
}