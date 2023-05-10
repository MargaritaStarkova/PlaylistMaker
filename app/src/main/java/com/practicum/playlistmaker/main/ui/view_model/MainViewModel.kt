package com.practicum.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.main.ui.models.ContentState

class MainViewModel : ViewModel() {
    
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel()
            }
        }
    }
    
    private val contentStateLiveData = MutableLiveData<ContentState>()
    
    fun observeContentStateLiveData(): LiveData<ContentState> = contentStateLiveData
    
    fun onSearchButtonClicked() {
        contentStateLiveData.value = ContentState.Search
    }
    fun onLibraryButtonClicked() {
        contentStateLiveData.value = ContentState.Library
    }
    fun onSettingsButtonClicked() {
        contentStateLiveData.value = ContentState.Settings
    }
}