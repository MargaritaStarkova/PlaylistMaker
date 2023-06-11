package com.practicum.playlistmaker.player.ui.models

import com.practicum.playlistmaker.R

sealed class PlayStatus(val imageResource: Int) {
    
    object Playing : PlayStatus(R.drawable.button_pause)
    object Paused : PlayStatus(R.drawable.button_play)
    
}
