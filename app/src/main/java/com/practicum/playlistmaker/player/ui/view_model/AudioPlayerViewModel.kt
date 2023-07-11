package com.practicum.playlistmaker.player.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.api.IMediaInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.models.PlayStatus
import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val mediaInteractor: IMediaInteractor,
    private val searchInteractor: ISearchInteractor,
) : ViewModel() {
    
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    private val playProgress get() = mediaInteractor.getPlayerPosition()
    private val playerState get() = mediaInteractor.getPlayerState()
    
    private val trackUrl = getTrack().previewUrl
    
    private var progressTimerJob: Job? = null
    private var preparePlayerJob: Job? = null
    
    init {
        preparingPlayer(trackUrl)
    }
    
    override fun onCleared() {
        super.onCleared()
        mediaInteractor.stopPlaying()
    }
    
    fun observePlayStatus(): LiveData<PlayStatus> = playStatusLiveData
    
    fun getTrack(): TrackModel {
        return searchInteractor.historyList.first()
    }
    
    fun onViewPaused() {
        pausePlaying()
    }
    
    fun playButtonClicked() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlaying()
            
            PlayerState.NOT_PREPARED -> {
                playStatusLiveData.value =
                    PlayStatus.Loading()
            }
            
            PlayerState.READY, PlayerState.PAUSED -> startPlaying()
            
            PlayerState.NOT_CONNECTED -> preparingPlayer(trackUrl)
        }
    }
    
    private fun preparingPlayer(url: String) {
        
        preparePlayerJob = viewModelScope.launch {
            mediaInteractor
                .preparePlayer(url)
                .collect { playerState ->
                    processPlayStatus(playerState)
                }
        }
    }
    
    private fun processPlayStatus(playerState: PlayerState) {
        when (playerState) {
            PlayerState.READY -> { playStatusLiveData.value = PlayStatus.Ready() }
            else -> { playStatusLiveData.value = PlayStatus.NotConnected(playProgress = playProgress) }
        }
    }
    
    private fun startPlaying() {
        
        mediaInteractor.startPlaying()
        progressTimerJob = viewModelScope.launch {
            do {
                playStatusLiveData.value = PlayStatus.Playing(playProgress = playProgress)
                delay(TIMER_DELAY)
            } while (playerState == PlayerState.PLAYING)
            
            if (playerState == PlayerState.READY) {
                pausePlaying()
            }
        }
    }

    
    private fun pausePlaying() {
        if (playerState == PlayerState.READY) {
            playStatusLiveData.value = PlayStatus.Paused(playProgress = START_POSITION)
        } else {
            mediaInteractor.pausePlaying()
            playStatusLiveData.value = PlayStatus.Paused(playProgress = playProgress)
        }
        progressTimerJob?.cancel()
    }
    
    companion object {
        const val START_POSITION = 0
        private const val TIMER_DELAY = 300L
    }
}