package com.practicum.playlistmaker.player.ui.models

import com.practicum.playlistmaker.R

sealed class PlayStatus(val imageResource: Int) {
    
    object Playing : PlayStatus(R.drawable.pause_button)
    object Paused : PlayStatus(R.drawable.play_button)
    
}
