package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.IAudioPlayer
import com.practicum.playlistmaker.player.domain.api.IMediaInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState

class MediaInteractor(
    
    private val player: IAudioPlayer,
) : IMediaInteractor {
    
    override fun startPlaying(trackUrl: String) {
        player.startPlayer(trackUrl)
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
}