package com.practicum.playlistmaker.player.ui.models

import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel

sealed class PlayStatus(val playProgress: Int) {
    class Loading : PlayStatus(playProgress = AudioPlayerViewModel.START_POSITION)
    class Ready : PlayStatus(playProgress = AudioPlayerViewModel.START_POSITION)
    class Playing(playProgress: Int) : PlayStatus(playProgress = playProgress)
    class Paused(playProgress: Int) : PlayStatus(playProgress = playProgress)
    class NotConnected(playProgress: Int) : PlayStatus(playProgress = playProgress)
}
