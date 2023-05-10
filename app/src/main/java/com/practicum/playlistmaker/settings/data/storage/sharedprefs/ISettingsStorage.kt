package com.practicum.playlistmaker.settings.data.storage.sharedprefs

import com.practicum.playlistmaker.settings.data.storage.models.SettingsDto

interface ISettingsStorage {
     fun getSettings(): SettingsDto
     fun saveSettings(settingsDto: SettingsDto)
}