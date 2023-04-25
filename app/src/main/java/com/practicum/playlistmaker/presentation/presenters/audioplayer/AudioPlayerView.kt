package com.practicum.playlistmaker.presentation.presenters.audioplayer

import com.practicum.playlistmaker.domain.models.TrackModel

interface AudioPlayerView {

    fun drawTrack(trackModel: TrackModel, startPosition: Int)
    fun updatePlayButton(imageResource: Int)
    fun updateTrackDuration(currentPositionMediaPlayer: Int)

}