package com.practicum.playlistmaker.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator

class App : Application() {
    
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val settingsInteractor = Creator.provideSettingsInteractor(this)
        darkTheme = settingsInteractor.getThemeSettings().darkTheme
    
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
    
    companion object {
        const val PREFERENCES = "app_preferences"
    }
}