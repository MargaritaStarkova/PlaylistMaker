package com.practicum.playlistmaker.library.data.repository

import com.practicum.playlistmaker.library.data.converter.TrackModelConverter
import com.practicum.playlistmaker.library.data.db.LocalDatabase
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.library.domain.api.ILibraryRepository
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryRepository(
    private val database: LocalDatabase,
    private val converter: TrackModelConverter,
) : ILibraryRepository {
    override fun saveTrack(track: TrackModel) {
        database
            .dao()
            .insertTrack(converter.mapToEntity(track))
    }
    
    override fun deleteTrack(track: TrackModel) {
        database
            .dao()
            .deleteTrack(converter.mapToEntity(track))
    }
    
    override fun getSelectedTracks(): Flow<List<TrackModel>> = database
        .dao()
        .getFavoriteTracks()
        .map { convertFromTrackEntity(it) }
    
    override fun isFavorite(id: String): Boolean = database
            .dao()
            .isFavorite(id)
    
    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<TrackModel> =
        tracks.map { converter.map(it) }
}