package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState

interface IMediaInteractor {
    fun startPlaying(trackUrl: String)
    fun pausePlaying()
    fun stopPlaying()
    fun getPlayerPosition(): Int
    fun getPlayerState(): PlayerState
}