package com.practicum.playlistmaker.core.di

import com.practicum.playlistmaker.library.ui.view_model.BottomSheetViewModel
import com.practicum.playlistmaker.library.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.playlist_creator.ui.view_model.PlaylistCreatorViewModel
import com.practicum.playlistmaker.playlist_menu.ui.view_model.PlaylistMenuViewModel
import com.practicum.playlistmaker.playlist_redactor.ui.view_model.PlaylistRedactorViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    
    viewModelOf(::AudioPlayerViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::FavoriteTracksViewModel)
    viewModelOf(::PlaylistsViewModel)
    viewModelOf(::PlaylistCreatorViewModel)
    viewModelOf(::BottomSheetViewModel)
    viewModelOf(::PlaylistMenuViewModel)
    viewModelOf(::PlaylistRedactorViewModel)
}