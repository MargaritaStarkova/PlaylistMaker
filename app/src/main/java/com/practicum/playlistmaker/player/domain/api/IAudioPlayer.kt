package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState

interface IAudioPlayer {

    var playerState: PlayerState
    fun getCurrentPosition(): Int
    fun startPlayer(url: String)
    fun pausePlayer()
    fun stopPlayer()
}