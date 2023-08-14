package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.LibraryInteractor
import com.practicum.playlistmaker.player.domain.api.MediaInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.models.PlayStatus
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val mediaInteractor: MediaInteractor,
    private val libraryInteractor: LibraryInteractor,
) : ViewModel() {
    
    private val _playStatusLiveData = MutableLiveData<PlayStatus>()
    private val _isFavoriteLiveData = MutableLiveData<Boolean>()
    
    val playStatusLiveData: LiveData<PlayStatus> = _playStatusLiveData
    val isFavoriteLiveData: LiveData<Boolean> = _isFavoriteLiveData
    
    private val playProgress get() = mediaInteractor.getPlayerPosition()
    private val playerState get() = mediaInteractor.getPlayerState()
    
    private var progressTimerJob: Job? = null
    private var preparePlayerJob: Job? = null
    private var isFavorite: Boolean = false
    
    override fun onCleared() {
        super.onCleared()
        progressTimerJob?.cancel()
        preparePlayerJob?.cancel()
        mediaInteractor.stopPlaying()
    }
    
    fun isFavorite(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
        
            libraryInteractor
                .isFavorite(id)
                .collect {
                    isFavorite = it
                    _isFavoriteLiveData.postValue(isFavorite)
                }
        
        }
    }
    
    fun onViewPaused() {
        pausePlaying()
    }
    
    fun toggleFavorite(track: TrackModel) {
        isFavorite = !isFavorite
        _isFavoriteLiveData.value = isFavorite
        viewModelScope.launch(Dispatchers.IO) {
            if (isFavorite) {
                libraryInteractor.likeTrack(track = track)
            }
            else {
                libraryInteractor.unLikeTrack(track = track)
            }
        }
    }
    
    fun playButtonClicked(url: String) {
        when (playerState) {
            PlayerState.PLAYING -> pausePlaying()
            
            PlayerState.NOT_PREPARED -> {
                _playStatusLiveData.value = PlayStatus.Loading()
            }
            
            PlayerState.READY, PlayerState.PAUSED -> startPlaying()
            
            PlayerState.NOT_CONNECTED -> preparingPlayer(url)
        }
    }
    
    fun preparingPlayer(url: String) {
        
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
            PlayerState.READY -> { _playStatusLiveData.value = PlayStatus.Ready() }
            else -> { _playStatusLiveData.value = PlayStatus.NotConnected(playProgress = playProgress) }
        }
    }
    
    private fun startPlaying() {
        
        mediaInteractor.startPlaying()
        progressTimerJob = viewModelScope.launch {
            do {
                _playStatusLiveData.value = PlayStatus.Playing(playProgress = playProgress)
                delay(TIMER_DELAY_MILLIS)
            } while (playerState == PlayerState.PLAYING)
            
            if (playerState == PlayerState.READY) {
                pausePlaying()
            }
        }
    }
    
    private fun pausePlaying() {
        if (playerState == PlayerState.READY) {
            _playStatusLiveData.value = PlayStatus.Paused(playProgress = START_POSITION)
        } else {
            mediaInteractor.pausePlaying()
            _playStatusLiveData.value = PlayStatus.Paused(playProgress = playProgress)
        }
        progressTimerJob?.cancel()
    }
    

    
    companion object {
        const val START_POSITION = 0
        private const val TIMER_DELAY_MILLIS = 300L
    }
}