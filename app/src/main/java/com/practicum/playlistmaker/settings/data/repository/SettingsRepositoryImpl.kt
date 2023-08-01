package com.practicum.playlistmaker.settings.data.repository

import com.practicum.playlistmaker.settings.data.storage.models.SettingsDto
import com.practicum.playlistmaker.settings.data.storage.sharedprefs.SettingsStorage
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.models.ThemeSettings

class SettingsRepositoryImpl(private val storage: SettingsStorage) : SettingsRepository {
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