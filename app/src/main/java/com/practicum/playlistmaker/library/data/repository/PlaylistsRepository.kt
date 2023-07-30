package com.practicum.playlistmaker.library.data.repository

import com.practicum.playlistmaker.library.data.converter.PlaylistModelConverter
import com.practicum.playlistmaker.library.data.db.LocalDatabase
import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.domain.api.IPlaylistsRepository
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import kotlinx.coroutines.flow.map

class PlaylistsRepository(
    private val database: LocalDatabase,
    private val converter: PlaylistModelConverter,
) : IPlaylistsRepository {
    
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
    
    override fun getSavedPlaylists() = database
        .playlistsDao()
        .getSavedPlaylists()
        .map { convertFromTrackEntity(it) }
    
    private fun convertFromTrackEntity(playlists: List<PlaylistEntity>): List<PlaylistModel> =
        playlists.map { converter.map(it) }
    
}