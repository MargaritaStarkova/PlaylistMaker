package com.practicum.playlistmaker.playlist_menu.domain.impl

import com.practicum.playlistmaker.playlist_menu.domain.api.PlaylistDurationCalculator
import com.practicum.playlistmaker.search.domain.models.TrackModel

class PlaylistDurationCalculatorImpl : PlaylistDurationCalculator {
    override fun getTracksDuration(trackList: List<TrackModel>): Int {
        
        return trackList.sumOf { it.trackTimeMillis } / MILLIS_IN_MINUTES
    }
    
    companion object {
        private const val MILLIS_IN_MINUTES = 60000
    }
}