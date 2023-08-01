package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.AudioPlayer
import com.practicum.playlistmaker.player.domain.api.MediaInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.flow.Flow

class MediaInteractorImpl(
    private val player: AudioPlayer,
) : MediaInteractor {
    
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