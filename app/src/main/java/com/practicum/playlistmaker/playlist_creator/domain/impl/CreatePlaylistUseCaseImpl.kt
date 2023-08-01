package com.practicum.playlistmaker.playlist_creator.domain.impl

import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.playlist_creator.domain.api.CreatePlaylistUseCase
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

class CreatePlaylistUseCaseImpl(
    private val repository: PlaylistsRepository,
) : CreatePlaylistUseCase {
    
    override suspend fun create(playlist: PlaylistModel) {
        repository.createPlaylist(playlist)
    }
}