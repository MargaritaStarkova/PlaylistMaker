package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.utils.router.HandlerRouter
import org.koin.dsl.module

val routerModule = module {
    
    factory {
        HandlerRouter()
    }
}