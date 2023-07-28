package com.practicum.playlistmaker.library.ui.models

import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

sealed class ScreenState(val content: List<PlaylistModel>) {
    
    object Empty : ScreenState(content = emptyList())
    
    class Content(playlists: List<PlaylistModel>) : ScreenState(content = playlists)
}