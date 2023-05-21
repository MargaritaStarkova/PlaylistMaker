package com.practicum.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.main.ui.models.NavigationState

class MainViewModel : ViewModel() {
    
    private val navigatinStateLiveData = SingleLiveEvent<NavigationState>()
    
    fun observeContentStateLiveData(): LiveData<NavigationState> = navigatinStateLiveData
    
    fun onSearchButtonClicked() {
        navigatinStateLiveData.value = NavigationState.Search
    }
    
    fun onLibraryButtonClicked() {
        navigatinStateLiveData.value = NavigationState.Library
    }
    
    fun onSettingsButtonClicked() {
        navigatinStateLiveData.value = NavigationState.Settings
    }
}