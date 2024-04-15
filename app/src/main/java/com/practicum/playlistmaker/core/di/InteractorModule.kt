package com.practicum.playlistmaker.core.di

import com.practicum.playlistmaker.library.domain.api.LibraryInteractor
import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.impl.LibraryInteractorImpl
import com.practicum.playlistmaker.library.domain.impl.PlaylistInteractorImpl
import com.practicum.playlistmaker.playlist_creator.domain.api.CreatePlaylistUseCase
import com.practicum.playlistmaker.playlist_creator.domain.impl.CreatePlaylistUseCaseImpl
import com.practicum.playlistmaker.playlist_menu.domain.api.PlaylistDurationCalculator
import com.practicum.playlistmaker.playlist_menu.domain.impl.PlaylistDurationCalculatorImpl
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val interactorModule = module {
    
    singleOf(::SearchInteractorImpl).bind<SearchInteractor>()
    singleOf(::SettingsInteractorImpl).bind<SettingsInteractor>()
    singleOf(::SharingInteractorImpl).bind<SharingInteractor>()
    singleOf(::LibraryInteractorImpl).bind<LibraryInteractor>()
    singleOf(::PlaylistInteractorImpl).bind<PlaylistsInteractor>()
    singleOf(::CreatePlaylistUseCaseImpl).bind<CreatePlaylistUseCase>()
    singleOf(::PlaylistDurationCalculatorImpl).bind<PlaylistDurationCalculator>()
}