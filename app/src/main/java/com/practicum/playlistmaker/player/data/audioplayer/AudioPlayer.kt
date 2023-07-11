package com.practicum.playlistmaker.player.data.audioplayer

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.IAudioPlayer
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.data.network.InternetConnectionValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class AudioPlayer(
    private val player: MediaPlayer,
    private val validator: InternetConnectionValidator,
) : IAudioPlayer {
    
    override var playerState = PlayerState.NOT_PREPARED
    
    override fun getCurrentPosition(): Int {
        return player.currentPosition
    }
    
    override fun startPlayer() {
        player.start()
        playerState = PlayerState.PLAYING
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
    
    override fun preparePlayer(url: String): Flow<PlayerState> = flow {
        emit(prepare(url))
    }
    
    private suspend fun prepare(url: String): PlayerState {
        
        if (!validator.isConnected()) {
            playerState = PlayerState.NOT_CONNECTED
        } else {
            withContext(Dispatchers.IO) {
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
        return playerState
    }
}