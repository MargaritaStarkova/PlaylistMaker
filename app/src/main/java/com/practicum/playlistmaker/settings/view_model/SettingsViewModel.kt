package com.practicum.playlistmaker.settings.view_model

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.ISettingsInteractor
import com.practicum.playlistmaker.settings.domain.models.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.api.ISharingInteractor

class SettingsViewModel(
    private val settingsInteractor: ISettingsInteractor,
    private val sharingInteractor: ISharingInteractor,
) : ViewModel() {
    
    private var darkTheme = false
    private val themeSwitcherStateLiveData = MutableLiveData(darkTheme)
    
    init {
        darkTheme = settingsInteractor.getThemeSettings().darkTheme
        themeSwitcherStateLiveData.value = darkTheme
    }
    
    fun observeThemeSwitcherState(): LiveData<Boolean> = themeSwitcherStateLiveData
    
    fun onThemeSwitcherClicked(isChecked: Boolean) {
        themeSwitcherStateLiveData.value = isChecked
        settingsInteractor.updateThemeSetting(ThemeSettings(darkTheme = isChecked))
        
        switchTheme(isChecked)
        Log.d("TEST", "+++ changeTheme = $isChecked +++")
    }
    
    fun onShareAppClicked() {
        sharingInteractor.shareApp()
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
}