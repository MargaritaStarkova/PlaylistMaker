package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.ui.view_model.MainViewModel
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    
    viewModel {
        MainViewModel()
    }
    
    viewModel {
        AudioPlayerViewModel(get(), get())
    }
    
    viewModel {
        SearchViewModel(get(), get())
    }
    
    viewModel {
        SettingsViewModel(get(), get())
    }
    
}