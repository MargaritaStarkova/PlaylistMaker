package com.practicum.playlistmaker.playlist_creator.domain.impl

import com.practicum.playlistmaker.library.domain.api.IPlaylistsRepository
import com.practicum.playlistmaker.playlist_creator.domain.api.ICreatePlaylistUseCase
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

class CreatePlaylistUseCase(
    private val repository: IPlaylistsRepository,
) : ICreatePlaylistUseCase {
    
    override suspend fun create(playlist: PlaylistModel) {
        repository.createPlaylist(playlist)
    }
}