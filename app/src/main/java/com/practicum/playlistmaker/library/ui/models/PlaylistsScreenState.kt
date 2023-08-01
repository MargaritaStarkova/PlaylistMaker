package com.practicum.playlistmaker.library.ui.models

import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

sealed class PlaylistsScreenState {
    
    object Empty : PlaylistsScreenState()
    
    class Content(val playlists: List<PlaylistModel>) : PlaylistsScreenState()
}