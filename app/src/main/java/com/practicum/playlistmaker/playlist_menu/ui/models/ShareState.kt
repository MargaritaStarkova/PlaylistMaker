package com.practicum.playlistmaker.playlist_menu.ui.models

import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

sealed interface ShareState {
    object Empty : ShareState
    class Content(val playlist: PlaylistModel) : ShareState
}