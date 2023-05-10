package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.application.App
import com.practicum.playlistmaker.player.data.audioplayer.AudioPlayerImpl
import com.practicum.playlistmaker.player.domain.api.AudioPlayer
import com.practicum.playlistmaker.player.domain.api.MediaInteractor
import com.practicum.playlistmaker.player.domain.impl.MediaInteractorImpl
import com.practicum.playlistmaker.search.data.network.INetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.repository.TrackRepository
import com.practicum.playlistmaker.search.data.storage.sharedprefs.SharedPrefsTracksStorage
import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.api.ITrackRepository
import com.practicum.playlistmaker.search.domain.impl.SearchInteractor
import com.practicum.playlistmaker.settings.data.repository.SettingsRepository
import com.practicum.playlistmaker.settings.data.storage.sharedprefs.ISettingsStorage
import com.practicum.playlistmaker.settings.data.storage.sharedprefs.SharedPrefsSettingsStorage
import com.practicum.playlistmaker.settings.domain.api.ISettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.ISettingsReporitory
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractor
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.IExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.ISharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractor

object Creator {
    
    fun provideSettingsInteractor(context: Context): ISettingsInteractor {
        return SettingsInteractor(repository = getSettingsRepository(context))
        
    }
    
    fun provideSharingInteractor(context: Context): ISharingInteractor {
        return SharingInteractor(
            externalNavigator = getExternalNavigator(context)
        )
    }
    
    fun provideMediaInteractor(trackUrl: String): MediaInteractor {
        return MediaInteractorImpl(
            player = getAudioPlayer(trackUrl)
        )
    }
    
    fun provideSearchInteractor(context: Context): ISearchInteractor {
        return SearchInteractor(
            repository = getTrackRepository(context)
        )
    }
    
    private fun getSettingsRepository(context: Context): ISettingsReporitory {
        return SettingsRepository(storage = getSettingsStorage(context))
    }
    
    private fun getSettingsStorage(context: Context): ISettingsStorage {
        return SharedPrefsSettingsStorage(
            sharedPreferences = getSharedPreferences(context)
        )
    }
    
    private fun getExternalNavigator(context: Context): IExternalNavigator {
        return ExternalNavigator(context = context)
    }
    
    private fun getAudioPlayer(trackUrl: String): AudioPlayer {
        return AudioPlayerImpl(url = trackUrl)
    }
    
    private fun getTrackRepository(context: Context): ITrackRepository {
        return TrackRepository(
            networkClient = getNetworkClient(context),
            tracksStorage = getTracksStorage(context),
        )
    }
    
    private fun getNetworkClient(context: Context): INetworkClient {
        return RetrofitNetworkClient(context = context)
    }
    
    private fun getTracksStorage(context: Context): SharedPrefsTracksStorage {
        return SharedPrefsTracksStorage(getSharedPreferences(context))
    }
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(App.PREFERENCES, AppCompatActivity.MODE_PRIVATE)
    }
}