package com.practicum.playlistmaker.settings.view_model

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.application.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.api.ISettingsInteractor
import com.practicum.playlistmaker.settings.domain.models.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.api.ISharingInteractor

class SettingsViewModel(
    private val settingsInteractor: ISettingsInteractor,
    private val sharingInteractor: ISharingInteractor,
    private val application: App,
) : AndroidViewModel(application) {
    
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as App
                SettingsViewModel(
                    settingsInteractor = Creator.provideSettingsInteractor(application),
                    sharingInteractor = Creator.provideSharingInteractor(application),
                    application = application,
                )
            }
        }
    }
    
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