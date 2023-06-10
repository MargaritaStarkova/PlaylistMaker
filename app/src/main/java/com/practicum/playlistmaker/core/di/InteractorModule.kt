package com.practicum.playlistmaker.core.di

import com.practicum.playlistmaker.player.domain.api.IMediaInteractor
import com.practicum.playlistmaker.player.domain.impl.MediaInteractor
import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchInteractor
import com.practicum.playlistmaker.settings.domain.api.ISettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.api.ISharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val interactorModule = module {
    
    singleOf(::MediaInteractor).bind<IMediaInteractor>()
    singleOf(::SearchInteractor).bind<ISearchInteractor>()
    singleOf(::SettingsInteractor).bind<ISettingsInteractor>()
    singleOf(::SharingInteractor).bind<ISharingInteractor>()
    
}