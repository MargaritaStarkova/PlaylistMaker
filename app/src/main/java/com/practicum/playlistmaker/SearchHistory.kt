package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.api.TrackData

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun saveHistory(historyList: ArrayList<TrackData>) {

        val json = Gson().toJson(historyList)
        sharedPreferences.edit()
            .putString(HISTORY_LIST_KEY, json)
            .apply()
    }

    fun readHistory(): Array<TrackData> {

        val json = sharedPreferences.getString(HISTORY_LIST_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<TrackData>::class.java)

    }


}