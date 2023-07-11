package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.flow.Flow

interface IMediaInteractor {
    fun startPlaying()
    fun pausePlaying()
    fun stopPlaying()
    fun getPlayerPosition(): Int
    fun getPlayerState(): PlayerState
    fun preparePlayer(url: String): Flow<PlayerState>
}