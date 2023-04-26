package com.practicum.playlistmaker.data.storage.sharedprefs

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.storage.models.TrackModelDto
import com.practicum.playlistmaker.presentation.ui.App

class SharedPrefsTracksStorage(private val sharedPreferences: SharedPreferences) : TracksStorage {

    override fun saveHistory(historyList: List<TrackModelDto>) {

        val json = Gson().toJson(historyList)

        sharedPreferences
            .edit()
            .putString(App.HISTORY_LIST_KEY, json)
            .apply()
    }

    override fun readHistory(): Array<TrackModelDto> {

        val json = sharedPreferences.getString(App.HISTORY_LIST_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<TrackModelDto>::class.java)
    }
}