package com.practicum.playlistmaker.search.data.storage.sharedprefs

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.storage.models.TrackModelDto

class SharedPrefsTracksStorage(private val sharedPreferences: SharedPreferences) : ITracksStorage {
    
    override fun saveHistory(historyList: List<TrackModelDto>) {
        Log.d("STORAGE", "+++save+++")
        val json = Gson().toJson(historyList)
        
        sharedPreferences
            .edit()
            .putString(HISTORY_LIST_KEY, json)
            .apply()
    }
    
    override fun readHistory(): Array<TrackModelDto> {
        Log.d("STORAGE", "+++read+++")
        
        val json = sharedPreferences.getString(HISTORY_LIST_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<TrackModelDto>::class.java)
    }
    
    companion object {
        private const val HISTORY_LIST_KEY = "history_preferences"
    }
}