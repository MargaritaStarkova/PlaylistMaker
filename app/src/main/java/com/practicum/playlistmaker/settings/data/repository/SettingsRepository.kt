package com.practicum.playlistmaker.settings.data.repository

import com.practicum.playlistmaker.settings.data.storage.models.SettingsDto
import com.practicum.playlistmaker.settings.data.storage.sharedprefs.ISettingsStorage
import com.practicum.playlistmaker.settings.domain.api.ISettingsReporitory
import com.practicum.playlistmaker.settings.domain.models.ThemeSettings

class SettingsRepository(private val storage: ISettingsStorage) : ISettingsReporitory {
    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            darkTheme = storage.getSettings().isDarkTheme
        )
    }
    
    override fun updateThemeSetting(settings: ThemeSettings) {
        storage.saveSettings(
            SettingsDto(
                isDarkTheme = settings.darkTheme
            )
        )
    }
    
}