package com.practicum.playlistmaker.playlist_creator.ui.models

sealed class ScreenState(
    val createBtnState: CreateBtnState = CreateBtnState.ENABLED
) {
    class Empty(
        createBtnState: CreateBtnState = CreateBtnState.DISABLED,
    ) : ScreenState(createBtnState)
    
    class HasContent(
        createBtnState: CreateBtnState = CreateBtnState.ENABLED,
    ) : ScreenState(createBtnState)
    
    object NeedsToAsk : ScreenState()
    
    object AllowedToGoOut : ScreenState()
}