package com.practicum.playlistmaker.core.di

import com.practicum.playlistmaker.library.data.repository.LibraryRepository
import com.practicum.playlistmaker.library.domain.api.ILibraryRepository
import com.practicum.playlistmaker.search.data.repository.TracksRepository
import com.practicum.playlistmaker.search.domain.api.ITrackRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepository
import com.practicum.playlistmaker.settings.domain.api.ISettingsReporitory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    
    singleOf(::TracksRepository).bind<ITrackRepository>()
    singleOf(::SettingsRepository).bind<ISettingsReporitory>()
    singleOf(::LibraryRepository).bind<ILibraryRepository>()
}