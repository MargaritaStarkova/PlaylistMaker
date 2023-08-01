package com.practicum.playlistmaker.core.di

import com.practicum.playlistmaker.library.data.repository.LibraryRepositoryImpl
import com.practicum.playlistmaker.library.domain.api.LibraryRepository
import com.practicum.playlistmaker.library.data.repository.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    
    singleOf(::TracksRepositoryImpl).bind<TrackRepository>()
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
    singleOf(::LibraryRepositoryImpl).bind<LibraryRepository>()
    singleOf(::PlaylistsRepositoryImpl).bind<PlaylistsRepository>()
}