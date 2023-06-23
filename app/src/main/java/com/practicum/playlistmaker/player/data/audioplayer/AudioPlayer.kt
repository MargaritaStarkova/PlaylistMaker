package com.practicum.playlistmaker.player.data.audioplayer

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.IAudioPlayer
import com.practicum.playlistmaker.player.domain.models.PlayerState

class AudioPlayer(private val player: MediaPlayer) : IAudioPlayer {
    
    override var playerState = PlayerState.NOT_PREPARED
    
    override fun getCurrentPosition(): Int {
        return player.currentPosition
    }
    
    override fun startPlayer(url: String) {
        if (playerState == PlayerState.NOT_PREPARED) {
            preparePlayer(url)
            
        }
        player.start()
        playerState = PlayerState.PLAYING
        
    }
    
    override fun pausePlayer() {
        player.pause()
        playerState = PlayerState.PAUSED
    }
    
    override fun stopPlayer() {
        player.apply {
            stop()
            reset()
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