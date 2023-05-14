package com.practicum.playlistmaker.di

import android.icu.text.SearchIterator
import com.practicum.playlistmaker.player.domain.api.IMediaInteractor
import com.practicum.playlistmaker.player.domain.impl.MediaInteractor
import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchInteractor
import com.practicum.playlistmaker.settings.domain.api.ISettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.api.ISharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractor
import org.koin.dsl.module

val interactorModule = module {
    
    single<IMediaInteractor> { (url: String) ->
        MediaInteractor(get(url))
    }
    
    single<ISearchInteractor> {
        SearchInteractor(get())
    }
    
    single<ISettingsInteractor> {
        SettingsInteractor(get())
    }
    
    single<ISharingInteractor> {
        SharingInteractor(get())
    }
}