package com.practicum.playlistmaker.main.ui.models

sealed interface NavigationState {
    object Search : NavigationState
    object Library : NavigationState
    object Settings : NavigationState
}