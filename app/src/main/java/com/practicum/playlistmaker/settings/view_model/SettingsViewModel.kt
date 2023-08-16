package com.practicum.playlistmaker.settings.view_model

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.models.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {
    
    private var darkTheme = false
    
    private val _themeSwitcherState = MutableLiveData(darkTheme)
    val themeSwitcherState: LiveData<Boolean> = _themeSwitcherState
    
    init {
        darkTheme = settingsInteractor.getThemeSettings().darkTheme
        _themeSwitcherState.value = darkTheme
    }
    
    fun onThemeSwitcherClicked(isChecked: Boolean) {
        _themeSwitcherState.value = isChecked
        settingsInteractor.updateThemeSetting(ThemeSettings(darkTheme = isChecked))
        
        switchTheme(isChecked)
    }
    
    fun onShareAppClicked() {
        sharingInteractor.share(getShareAppLink())
    }
    
    fun onWriteSupportClicked() {
        sharingInteractor.openSupport()
    }
    
    fun termsOfUseClicked() {
        sharingInteractor.openTerms()
    }
    
    private fun switchTheme(darkThemeEnabled: Boolean) {
    
        darkTheme = darkThemeEnabled
    
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
    
    private fun getShareAppLink(): String {
        return APP_LINK
    }
    
    companion object {
        private const val APP_LINK = "https://practicum.yandex.ru/android-developer/"
    }
}