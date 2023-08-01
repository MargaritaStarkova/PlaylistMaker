package com.practicum.playlistmaker.search.data.storage.sharedprefs

import com.practicum.playlistmaker.search.data.storage.models.TrackModelDto

interface TracksStorage {
    fun saveHistory(historyList: List<TrackModelDto>)
    fun readHistory(): List<TrackModelDto>
}