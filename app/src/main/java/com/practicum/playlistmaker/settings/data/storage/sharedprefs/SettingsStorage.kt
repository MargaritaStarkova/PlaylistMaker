package com.practicum.playlistmaker.settings.data.storage.sharedprefs

import com.practicum.playlistmaker.settings.data.storage.models.SettingsDto

interface SettingsStorage {
     fun getSettings(): SettingsDto
     fun saveSettings(settingsDto: SettingsDto)
}