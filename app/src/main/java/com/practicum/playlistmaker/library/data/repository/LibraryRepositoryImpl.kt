package com.practicum.playlistmaker.library.data.repository

import com.practicum.playlistmaker.library.data.converter.TrackModelConverter
import com.practicum.playlistmaker.library.data.db.LocalDatabase
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.library.domain.api.LibraryRepository
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryRepositoryImpl(
    private val database: LocalDatabase,
    private val converter: TrackModelConverter,
) : LibraryRepository {
    override suspend fun saveTrack(track: TrackModel) {
        database
            .selectedTracksDao()
            .insertTrack(converter.mapToEntity(track))
    }
    
    override suspend fun deleteTrack(track: TrackModel) {
        database
            .selectedTracksDao()
            .deleteTrack(converter.mapToEntity(track))
    }
    
    override fun getSelectedTracks(): Flow<List<TrackModel>> {
        return database
            .selectedTracksDao()
            .getFavoriteTracks()
            .map { convertFromTrackEntity(it) }
    }
    
    override fun isFavorite(id: String): Flow<Boolean> {
        return database
            .selectedTracksDao()
            .isFavorite(id)
    }
    
    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<TrackModel> {
        return tracks.map { converter.map(it) }
    }
}