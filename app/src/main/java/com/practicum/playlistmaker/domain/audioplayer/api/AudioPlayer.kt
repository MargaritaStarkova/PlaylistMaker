package com.practicum.playlistmaker.domain.audioplayer.api

import com.practicum.playlistmaker.domain.models.PlayerState

interface AudioPlayer {

    var playerState: PlayerState
    fun getCurrentPosition(): Int
    fun startPlayer()
    fun pausePlayer()
    fun release()
}