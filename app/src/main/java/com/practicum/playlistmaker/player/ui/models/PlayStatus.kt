package com.practicum.playlistmaker.player.ui.models

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel

sealed class PlayStatus(val imageResource: Int, val playProgress: Int) {
    
    class Loading : PlayStatus(
        imageResource = R.drawable.button_play_not_prepared,
        playProgress = AudioPlayerViewModel.START_POSITION
    )
    
    class Ready :
        PlayStatus(imageResource = R.drawable.button_play, playProgress = AudioPlayerViewModel.START_POSITION)
    
    class Playing(playProgress: Int) :
        PlayStatus(imageResource = R.drawable.button_pause, playProgress = playProgress)
    
    class Paused(playProgress: Int) :
        PlayStatus(imageResource = R.drawable.button_play, playProgress = playProgress)
    
    class NotConnected(playProgress: Int) :
        PlayStatus(imageResource = R.drawable.button_play, playProgress = playProgress)
}
