package com.practicum.playlistmaker.library.ui.models

import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

sealed class PlaylistsScreenState(val content: List<PlaylistModel>) {
    
    object Empty : PlaylistsScreenState(content = emptyList())
    
    class Content(playlists: List<PlaylistModel>) : PlaylistsScreenState(content = playlists)
}