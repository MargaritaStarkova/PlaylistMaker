package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.ISettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.ISettingsReporitory
import com.practicum.playlistmaker.settings.domain.models.ThemeSettings

class SettingsInteractor(private val repository: ISettingsReporitory) : ISettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }
    
    override fun updateThemeSetting(settings: ThemeSettings) {
        repository.updateThemeSetting(settings)
    }
    
}