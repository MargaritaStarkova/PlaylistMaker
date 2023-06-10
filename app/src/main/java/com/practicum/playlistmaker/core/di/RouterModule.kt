package com.practicum.playlistmaker.core.di

import com.practicum.playlistmaker.core.utils.router.HandlerRouter
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val routerModule = module {
    
    factoryOf(::HandlerRouter)
}