package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.IAudioPlayer
import com.practicum.playlistmaker.player.domain.api.IMediaInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.flow.Flow

class MediaInteractor(
    
    private val player: IAudioPlayer,
) : IMediaInteractor {
    
    override fun startPlaying() {
        player.startPlayer()
    }
    
    override fun pausePlaying() {
        player.pausePlayer()
    }
    
    override fun stopPlaying() {
        player.stopPlayer()
    }

    override fun getPlayerPosition(): Int {
        return player.getCurrentPosition()
    }

    override fun getPlayerState(): PlayerState {
        return player.playerState
    }
    
    override fun preparePlayer(url: String): Flow<PlayerState> {
        return player.preparePlayer(url)
    }
}