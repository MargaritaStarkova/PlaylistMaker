package com.practicum.playlistmaker.presentation.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        const val PREFERENCES = "app_preferences"
        const val SWITCH_THEME_KEY = "theme_preferences"
        const val HISTORY_LIST_KEY = "history_preferences"
    }

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        darkTheme =
            getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(SWITCH_THEME_KEY, false)

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {

        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun saveTheme(darkThemeEnabled: Boolean) {
        getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .edit()
            .putBoolean(SWITCH_THEME_KEY, darkThemeEnabled)
            .apply()
    }
}