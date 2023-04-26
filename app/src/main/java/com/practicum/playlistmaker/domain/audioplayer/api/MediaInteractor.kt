package com.practicum.playlistmaker.domain.audioplayer.api

import com.practicum.playlistmaker.domain.models.PlayerState

interface MediaInteractor {
    fun startPlaying()
    fun pausePlaying()
    fun stopPlaying()
    fun getPlayerPosition(): Int
    fun getPlayerState(): PlayerState
}