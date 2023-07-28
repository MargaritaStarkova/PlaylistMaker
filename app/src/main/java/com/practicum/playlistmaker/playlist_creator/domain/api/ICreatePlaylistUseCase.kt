package com.practicum.playlistmaker.playlist_creator.domain.api

import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

interface ICreatePlaylistUseCase {
    suspend fun create(playlist: PlaylistModel)
}