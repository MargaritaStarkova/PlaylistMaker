package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.ui.view_model.MainViewModel
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val viewModelModule = module {
    
    viewModelOf(::MainViewModel).bind()
    viewModelOf(::AudioPlayerViewModel).bind()
    viewModelOf(::SearchViewModel).bind()
    viewModelOf(::SettingsViewModel).bind()
    
}