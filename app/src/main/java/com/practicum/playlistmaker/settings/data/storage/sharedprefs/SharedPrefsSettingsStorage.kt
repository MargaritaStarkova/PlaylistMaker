package com.practicum.playlistmaker.settings.data.storage.sharedprefs

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.data.storage.models.SettingsDto

class SharedPrefsSettingsStorage(private val sharedPreferences: SharedPreferences) :
    ISettingsStorage {
    
    companion object {
        const val SWITCH_THEME_KEY = "theme_preferences"
    }
    
    override fun getSettings(): SettingsDto {
        return SettingsDto(
            isDarkTheme = sharedPreferences.getBoolean(SWITCH_THEME_KEY, false)
        )
    }
    
    override fun saveSettings(settingsDto: SettingsDto) {
        sharedPreferences
                .edit()
                .putBoolean(SWITCH_THEME_KEY, settingsDto.isDarkTheme)
                .apply()
    }
}