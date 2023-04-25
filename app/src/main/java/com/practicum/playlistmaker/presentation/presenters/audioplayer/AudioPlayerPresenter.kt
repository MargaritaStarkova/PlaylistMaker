package com.practicum.playlistmaker.presentation.presenters.audioplayer

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.audioplayer.api.MediaInteractor
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.TrackModel
import com.practicum.playlistmaker.presentation.presenters.router.HandlerRouter
import com.practicum.playlistmaker.presentation.presenters.router.NavigationRouter

class AudioPlayerPresenter(
    private val view: AudioPlayerView,
    private val navigationRouter: NavigationRouter,
    private val mediaInteractor: MediaInteractor,

    ) {
    companion object {
        private const val START_POSITION = 0
    }

    private val track: TrackModel = navigationRouter.getTrackInfo()
    private val handlerRouter = HandlerRouter()

    init {
        view.drawTrack(trackModel = track, startPosition = START_POSITION)
    }

    private fun startPlaying() {

        view.updatePlayButton(imageResource = R.drawable.pause_button)
        mediaInteractor.startPlaying()
        handlerRouter.startPlaying(object : Runnable {
            override fun run() {
                if (mediaInteractor.getPlayerState() == PlayerState.READY) {
                    view.updateTrackDuration(currentPositionMediaPlayer = 0)
                    view.updatePlayButton(imageResource = R.drawable.play_button)
                    handlerRouter.stopRunnable()
                } else {
                    view.updateTrackDuration(currentPositionMediaPlayer = mediaInteractor.getPlayerPosition())
                    handlerRouter.startPlaying(this)
                }
            }
        })
    }

    private fun pausePlaying() {
        mediaInteractor.pausePlaying()
        view.updatePlayButton(imageResource = R.drawable.play_button)
        handlerRouter.stopRunnable()
    }

    fun onViewPaused() {
        pausePlaying()
    }

    fun onViewDestroyed() {
        mediaInteractor.stopPlaying()
        handlerRouter.stopRunnable()
    }

    fun backButtonClicked() {
        navigationRouter.goBack()
    }

    fun playButtonClicked() {

        when (mediaInteractor.getPlayerState()) {
            PlayerState.PLAYING -> pausePlaying()
            else -> startPlaying()
        }
    }
}