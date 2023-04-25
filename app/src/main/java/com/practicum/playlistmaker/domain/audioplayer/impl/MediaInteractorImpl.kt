package com.practicum.playlistmaker.domain.audioplayer.impl

import com.practicum.playlistmaker.domain.audioplayer.api.AudioPlayer
import com.practicum.playlistmaker.domain.audioplayer.api.MediaInteractor
import com.practicum.playlistmaker.domain.models.PlayerState

class MediaInteractorImpl(
    private val player: AudioPlayer
) : MediaInteractor {

    override fun startPlaying() {
        player.startPlayer()
    }

    override fun pausePlaying() {
        player.pausePlayer()
    }

    override fun stopPlaying() {
        player.release()
    }

    override fun getPlayerPosition(): Int {
        return player.getCurrentPosition()
    }

    override fun getPlayerState(): PlayerState {
        return player.playerState
    }
}