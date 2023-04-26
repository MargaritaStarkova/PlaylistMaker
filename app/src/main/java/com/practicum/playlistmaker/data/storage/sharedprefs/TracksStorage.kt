package com.practicum.playlistmaker.data.storage.sharedprefs

import com.practicum.playlistmaker.data.storage.models.TrackModelDto

interface TracksStorage {
    fun saveHistory(historyList: List<TrackModelDto>)
    fun readHistory(): Array<TrackModelDto>
}