package com.practicum.playlistmaker.core.di

import com.practicum.playlistmaker.library.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val viewModelModule = module {
    
    viewModelOf(::AudioPlayerViewModel).bind()
    viewModelOf(::SearchViewModel).bind()
    viewModelOf(::SettingsViewModel).bind()
    viewModelOf(::FavoriteTracksViewModel).bind()
    viewModelOf(::PlaylistsViewModel).bind()
}