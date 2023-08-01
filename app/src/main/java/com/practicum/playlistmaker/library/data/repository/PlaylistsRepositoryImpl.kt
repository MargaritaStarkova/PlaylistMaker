package com.practicum.playlistmaker.library.data.repository

import com.practicum.playlistmaker.library.data.converter.PlaylistModelConverter
import com.practicum.playlistmaker.library.data.db.LocalDatabase
import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val database: LocalDatabase,
    private val converter: PlaylistModelConverter,
) : PlaylistsRepository {
    
    override suspend fun createPlaylist(playlist: PlaylistModel) {
        database
            .playlistsDao()
            .insertPlaylist(converter.map(playlist))
    }
    
    override suspend fun deletePlaylist(playlist: PlaylistModel) {
        database
            .playlistsDao()
            .deletePlaylist(converter.map(playlist))
    }
    
    override suspend fun updateTracks(playlist: PlaylistModel) {
        database
            .playlistsDao()
            .updatePlaylist(converter.map(playlist))
    }
    
    override fun getSavedPlaylists(): Flow<List<PlaylistModel>> {
        return database
            .playlistsDao()
            .getSavedPlaylists()
            .map { convertFromTrackEntity(it) }
    }
    
    private fun convertFromTrackEntity(playlists: List<PlaylistEntity>): List<PlaylistModel> {
        return playlists.map { converter.map(it) }
    }
}