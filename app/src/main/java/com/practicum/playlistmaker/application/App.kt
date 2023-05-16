package com.practicum.playlistmaker.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.routerModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.api.ISettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, routerModule, viewModelModule)
        }
    
        darkTheme = getKoin()
            .get<ISettingsInteractor>()
            .getThemeSettings().darkTheme
    
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
    
    companion object {
        const val PREFERENCES = "app_preferences"
    }
}