package com.practicum.playlistmaker.player.ui.view_model

import com.practicum.playlistmaker.search.domain.models.TrackModel

interface AudioPlayerView {

    fun drawTrack(trackModel: TrackModel, startPosition: Int)
    fun updatePlayButton(imageResource: Int)
    fun updateTrackDuration(currentPositionMediaPlayer: Int)

}