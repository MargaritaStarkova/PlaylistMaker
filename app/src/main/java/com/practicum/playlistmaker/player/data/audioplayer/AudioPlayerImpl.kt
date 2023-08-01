package com.practicum.playlistmaker.player.data.audioplayer

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.AudioPlayer
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.data.network.InternetConnectionValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException

class AudioPlayerImpl(
    private val player: MediaPlayer,
    private val validator: InternetConnectionValidator,
) : AudioPlayer {
    
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
            if (playerState == PlayerState.PLAYING) {
                pause()
            }
            if (playerState == PlayerState.PAUSED) {
                stop()
            }
            reset()
            setOnCompletionListener(null)
        }
        playerState = PlayerState.NOT_PREPARED
    }
    
    override fun preparePlayer(url: String): Flow<PlayerState> = flow {
        if (playerState == PlayerState.NOT_PREPARED || playerState == PlayerState.NOT_CONNECTED) {
            emit(prepare(url))
        }
    }
    
    private suspend fun prepare(url: String): PlayerState {
       try {
           if (!validator.isConnected()) {
               playerState = PlayerState.NOT_CONNECTED
           } else {
               withContext(Dispatchers.IO) {
                   player.apply {
                       reset()
                       setOnCompletionListener {
                           playerState = PlayerState.READY
                       }
                       setDataSource(url)
                       prepare()
                   }
                   playerState = PlayerState.READY
               }
           }
       } catch (e: IOException) {
           stopPlayer()
       }
        
        return playerState
    }
}