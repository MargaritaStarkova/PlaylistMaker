package com.practicum.playlistmaker.data.audioplayer

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.audioplayer.api.AudioPlayer
import com.practicum.playlistmaker.domain.models.PlayerState

class AudioPlayerImpl(url: String) : AudioPlayer {

    override var playerState = PlayerState.NOT_PREPARED
    private val player = MediaPlayer()

    init {
        player.apply {
            setDataSource(url)
            setOnCompletionListener {
                playerState = PlayerState.READY
            }
        }
    }

    override fun getCurrentPosition(): Int {
        return player.currentPosition
    }

    override fun startPlayer() {
        if (playerState == PlayerState.NOT_PREPARED){
            player.prepare()
            playerState = PlayerState.READY
        }
        player.start()
        playerState = PlayerState.PLAYING

    }

    override fun pausePlayer() {
        player.pause()
        playerState = PlayerState.PAUSED
    }

    override fun release() {
        player.release()
    }
}