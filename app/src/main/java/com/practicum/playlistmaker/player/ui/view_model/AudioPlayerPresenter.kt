package com.practicum.playlistmaker.player.ui.view_model

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.api.MediaInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.utils.router.HandlerRouter
import com.practicum.playlistmaker.utils.router.NavigationRouter

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


}