package com.practicum.playlistmaker.playlist_creator.ui.models

sealed class PlaylistCreatorState(
    val createBtnState: CreateBtnState = CreateBtnState.ENABLED,
) {
    class Empty(
        createBtnState: CreateBtnState = CreateBtnState.DISABLED,
    ) : PlaylistCreatorState(createBtnState)
    
    class HasContent(
        createBtnState: CreateBtnState = CreateBtnState.ENABLED,
    ) : PlaylistCreatorState(createBtnState)
    
    object NeedsToAsk : PlaylistCreatorState()
    
    object AllowedToGoOut : PlaylistCreatorState()
}