package com.practicum.playlistmaker.player.ui.models

import com.practicum.playlistmaker.R

sealed class PlayStatus(val imageResource: Int, val playProgress: Int) {
    
    class Playing(playProgress: Int) : PlayStatus(imageResource = R.drawable.button_pause, playProgress = playProgress)
    class Paused(playProgress: Int) : PlayStatus(imageResource = R.drawable.button_play, playProgress = playProgress)
    class NotConnected(playProgress: Int) : PlayStatus(imageResource = R.drawable.button_play, playProgress = playProgress)
}
