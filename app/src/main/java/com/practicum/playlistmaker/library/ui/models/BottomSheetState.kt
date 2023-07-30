package com.practicum.playlistmaker.library.ui.models

import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

sealed class BottomSheetState(
    val content: List<PlaylistModel> = emptyList(),
) {
    
    object Empty : BottomSheetState()
    
    class AddedNow(val playlistModel: PlaylistModel) : BottomSheetState()
    
    class AddedAlready(val playlistModel: PlaylistModel) : BottomSheetState()
    
    class Content(playlists: List<PlaylistModel>) : BottomSheetState(content = playlists)
}