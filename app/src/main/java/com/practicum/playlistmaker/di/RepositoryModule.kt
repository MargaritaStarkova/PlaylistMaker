package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.data.repository.TrackRepository
import com.practicum.playlistmaker.search.domain.api.ITrackRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepository
import com.practicum.playlistmaker.settings.domain.api.ISettingsReporitory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    
    singleOf(::TrackRepository).bind<ITrackRepository>()
    singleOf(::SettingsRepository).bind<ISettingsReporitory>()
}