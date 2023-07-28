package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.api.IPlaylistsInteractor
import com.practicum.playlistmaker.library.domain.api.IPlaylistsRepository
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import kotlinx.coroutines.flow.Flow

class PlaylistInteractor(
    private val repository: IPlaylistsRepository,
) : IPlaylistsInteractor {
    
    override fun getPlaylists(): Flow<List<PlaylistModel>> =
        repository.getSavedPlaylists()
}