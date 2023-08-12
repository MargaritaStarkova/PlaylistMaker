package com.practicum.playlistmaker.playlist_menu.ui.models

import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

sealed interface PlaylistMenuState {
    object DefaultState : PlaylistMenuState
    object EmptyShare : PlaylistMenuState
    data class Content(val content: PlaylistModel) : PlaylistMenuState
    class Share(val playlist: PlaylistModel) : PlaylistMenuState
}