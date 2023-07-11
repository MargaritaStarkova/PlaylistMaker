package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.flow.Flow

interface IAudioPlayer {

    var playerState: PlayerState
    fun getCurrentPosition(): Int
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun preparePlayer(url: String): Flow<PlayerState>
}