package com.practicum.playlistmaker.player.data.audioplayer

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.IAudioPlayer
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.data.network.InternetConnectionValidator

class AudioPlayer(
    private val player: MediaPlayer,
    private val validator: InternetConnectionValidator,
) : IAudioPlayer {
    
    override var playerState = PlayerState.NOT_PREPARED
    
    override fun getCurrentPosition(): Int {
        return player.currentPosition
    }
    
    override fun startPlayer(url: String) {
        when (playerState) {
            PlayerState.NOT_PREPARED, PlayerState.NOT_CONNECTED -> {
                if (validator.isConnected()) {
                    preparePlayer(url)
                    playerState = PlayerState.PLAYING
                    player.start()
                    
                } else playerState = PlayerState.NOT_CONNECTED
            }
            
            else -> {
                player.start()
                playerState = PlayerState.PLAYING
            }
        }
    }
    
    override fun pausePlayer() {
        if (playerState == PlayerState.PLAYING) {
            player.pause()
            playerState = PlayerState.PAUSED
        }
    }
    
    override fun stopPlayer() {
        player.apply {
            stop()
            reset()
            setOnCompletionListener(null)
        }
        playerState = PlayerState.NOT_PREPARED
    }
    
    private fun preparePlayer(url: String) {
        player.apply {
            reset()
            setDataSource(url)
            prepare()
            setOnCompletionListener {
                playerState = PlayerState.READY
            }
        }
       playerState = PlayerState.READY
    }
}