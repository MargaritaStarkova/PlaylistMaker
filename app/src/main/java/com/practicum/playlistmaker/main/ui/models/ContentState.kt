package com.practicum.playlistmaker.main.ui.models

sealed interface ContentState {
    object Search : ContentState
    object Library : ContentState
    object Settings : ContentState
}