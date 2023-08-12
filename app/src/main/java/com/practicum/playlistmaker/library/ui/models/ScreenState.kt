package com.practicum.playlistmaker.library.ui.models

sealed interface ScreenState {
    
    object Empty : ScreenState
    
    class Content<T>(val contentList: List<T>) : ScreenState
}