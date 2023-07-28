package com.practicum.playlistmaker.library.domain.api

import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import kotlinx.coroutines.flow.Flow

interface IPlaylistsInteractor {
    
    fun getPlaylists(): Flow<List<PlaylistModel>>
}