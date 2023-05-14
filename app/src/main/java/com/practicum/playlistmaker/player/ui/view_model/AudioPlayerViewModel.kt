package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.di.Creator
import com.practicum.playlistmaker.player.domain.api.IMediaInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.models.PlayStatus
import com.practicum.playlistmaker.utils.router.HandlerRouter

class AudioPlayerViewModel(
    private val mediaInteractor: IMediaInteractor,
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
    }
}