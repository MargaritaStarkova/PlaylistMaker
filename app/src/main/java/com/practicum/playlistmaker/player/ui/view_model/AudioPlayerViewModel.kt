package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.utils.router.HandlerRouter
import com.practicum.playlistmaker.player.domain.api.IMediaInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.domain.models.PlayerStatePractice
import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val mediaInteractor: IMediaInteractor,
    private val searchInteractor: ISearchInteractor,
) : ViewModel() {
    
    // private val playStatusLiveData = MutableLiveData<PlayStatus>()
    // private val playProgressLiveData = MutableLiveData<Int>()
    
    private val _playerStatePractice =
        MutableLiveData<PlayerStatePractice>(PlayerStatePractice.Default())
    val playerStatePractice: LiveData<PlayerStatePractice> get() = _playerStatePractice
    
    private var timerJob: Job? = null
    
    override fun onCleared() {
        super.onCleared()
        mediaInteractor.stopPlaying()
        
    }
    
    //  fun observePlayStatus(): LiveData<PlayStatus> = playStatusLiveData
    // fun observePlayProgress(): LiveData<Int> = playProgressLiveData
    
    fun onViewPaused() {
        pausePlaying()
    }
    
    fun playButtonClicked(trackUrl: String) {
        when (mediaInteractor.getPlayerState()) {
            PlayerState.PLAYING -> pausePlaying()
            else -> startPlaying(trackUrl)
        }
    }
    
    private fun startPlaying(trackUrl: String) {
        
        //   playStatusLiveData.value = PlayStatus.Playing
        
        mediaInteractor.startPlaying(trackUrl)
        
        timerJob = viewModelScope.launch {
            while (mediaInteractor.getPlayerState() == PlayerState.PLAYING) {
                delay(500L)
                _playerStatePractice.value = PlayerStatePractice.Playing(
                    mediaInteractor
                        .getPlayerPosition()
                )
            }
            
            if (mediaInteractor.getPlayerState() == PlayerState.READY) {
                //   playProgressLiveData.value = START_POSITION
                //   playStatusLiveData.value = PlayStatus.Paused
        
                _playerStatePractice.value = PlayerStatePractice.Paused(START_POSITION)
            }
        }
        
    }
    
    private fun pausePlaying() {
        
        mediaInteractor.pausePlaying()
        timerJob?.cancel()
        
        //  playStatusLiveData.value = PlayStatus.Paused
        _playerStatePractice.value = PlayerStatePractice.Paused(
            mediaInteractor
                .getPlayerPosition()
        )
        
    }
    
    fun getTrack(): TrackModel {
        return searchInteractor
            .getTracksFromHistory()
            .first()
    }
    
    companion object {
        const val START_POSITION = 0
    }
}