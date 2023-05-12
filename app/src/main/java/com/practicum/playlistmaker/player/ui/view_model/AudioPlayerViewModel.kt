package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.api.MediaInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.models.PlayStatus
import com.practicum.playlistmaker.utils.router.HandlerRouter

class AudioPlayerViewModel(
    private val mediaInteractor: MediaInteractor,
    private val handlerRouter: HandlerRouter
) : ViewModel() {
    
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    private val playProgressLiveData = MutableLiveData<Int>()
    
    override fun onCleared() {
        super.onCleared()
        mediaInteractor.pausePlaying()
        mediaInteractor.stopPlaying()
        handlerRouter.stopRunnable()
    }
    
    fun observePlayStatus(): LiveData<PlayStatus> = playStatusLiveData
    fun observePlayProgress(): LiveData<Int> = playProgressLiveData
    
    fun onViewPaused() {
        pausePlaying()
    }
    
    fun playButtonClicked() {
        when (mediaInteractor.getPlayerState()) {
            PlayerState.PLAYING -> pausePlaying()
            else -> startPlaying()
        }
    }
    
    private fun startPlaying() {
        
        playStatusLiveData.value = PlayStatus.Playing
        mediaInteractor.startPlaying()
        
        handlerRouter.startPlaying(object : Runnable {
            override fun run() {
                if (mediaInteractor.getPlayerState() == PlayerState.READY) {
                    playProgressLiveData.value = START_POSITION
                    playStatusLiveData.value = PlayStatus.Paused
                    handlerRouter.stopRunnable()
                } else {
                    playProgressLiveData.value = mediaInteractor.getPlayerPosition()
                    handlerRouter.startPlaying(this)
                }
            }
        })
    }
    
    private fun pausePlaying() {
        mediaInteractor.pausePlaying()
        playStatusLiveData.value = PlayStatus.Paused
        handlerRouter.stopRunnable()
    }
    
    companion object {
        const val START_POSITION = 0
        
        fun getViewModelFactory(trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(
                    mediaInteractor = Creator.provideMediaInteractor(trackUrl),
                    handlerRouter = HandlerRouter(),
                )
            }
        }
    }
}