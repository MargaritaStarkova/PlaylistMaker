package com.practicum.playlistmaker.playlist_menu.domain.api

import com.practicum.playlistmaker.search.domain.models.TrackModel

interface PlaylistDurationCalculator {
    
    fun getTracksDuration(trackList: List<TrackModel>): Int
}