package com.practicum.playlistmaker.di

import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.application.App
import com.practicum.playlistmaker.player.data.audioplayer.AudioPlayer
import com.practicum.playlistmaker.player.domain.api.IAudioPlayer
import com.practicum.playlistmaker.search.data.network.INetworkClient
import com.practicum.playlistmaker.search.data.network.ITunesApi
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.storage.sharedprefs.ITracksStorage
import com.practicum.playlistmaker.search.data.storage.sharedprefs.SharedPrefsTracksStorage
import com.practicum.playlistmaker.settings.data.storage.sharedprefs.ISettingsStorage
import com.practicum.playlistmaker.settings.data.storage.sharedprefs.SharedPrefsSettingsStorage
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.IExternalNavigator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://itunes.apple.com/"

val dataModule = module {
    
    single<ITunesApi> {
    
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
    
    
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient).build().create(ITunesApi::class.java)
    }
    
    single {
        androidContext().getSharedPreferences(App.PREFERENCES, AppCompatActivity.MODE_PRIVATE)
    }
    
    single<INetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }
    
    single<ITracksStorage> {
        SharedPrefsTracksStorage(get())
    }
    
    single<ISettingsStorage> {
        SharedPrefsSettingsStorage(get())
    }
    
    single<IAudioPlayer> { (url: String) ->
        AudioPlayer(url)
    }
    
    single<IExternalNavigator> {
        ExternalNavigator(androidContext())
    }
}