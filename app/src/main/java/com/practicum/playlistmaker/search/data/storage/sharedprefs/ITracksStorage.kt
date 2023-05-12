package com.practicum.playlistmaker.search.data.storage.sharedprefs

import com.practicum.playlistmaker.search.data.storage.models.TrackModelDto

interface ITracksStorage {
    fun saveHistory(historyList: List<TrackModelDto>)
    fun readHistory(): Array<TrackModelDto>
}