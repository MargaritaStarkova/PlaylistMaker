package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    
    private var progressTimer: Job? = null
    
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
    
    fun playButtonClicked(trackUrl: String) {
        when (playerState) {
            PlayerState.PLAYING -> pausePlaying()
            else -> startPlaying(trackUrl)
        }
    }
    
    private fun startPlaying(trackUrl: String) {
    
        mediaInteractor.startPlaying(trackUrl)
    
        if (playerState == PlayerState.NOT_CONNECTED) {
            playStatusLiveData.value = PlayStatus.NotConnected(playProgress = playProgress)
        } else {
            progressTimer = viewModelScope.launch {
                do {
                    playStatusLiveData.value = PlayStatus.Playing(playProgress = playProgress)
                    delay(TIMER_DELAY)
                }
                while (playerState == PlayerState.PLAYING)
            
                if (playerState == PlayerState.READY) {
                    playStatusLiveData.value = PlayStatus.Paused(playProgress = START_POSITION)
                }
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
        progressTimer?.cancel()
    }
    
    companion object {
        const val START_POSITION = 0
        private const val TIMER_DELAY = 300L
    }
}