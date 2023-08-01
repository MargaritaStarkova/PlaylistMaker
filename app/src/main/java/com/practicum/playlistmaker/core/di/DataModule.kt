package com.practicum.playlistmaker.core.di

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.practicum.playlistmaker.core.application.App
import com.practicum.playlistmaker.library.data.converter.PlaylistModelConverter
import com.practicum.playlistmaker.library.data.converter.TrackModelConverter
import com.practicum.playlistmaker.library.data.db.LocalDatabase
import com.practicum.playlistmaker.player.data.audioplayer.AudioPlayerImpl
import com.practicum.playlistmaker.player.domain.api.AudioPlayer
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.ITunesApi
import com.practicum.playlistmaker.search.data.network.InternetConnectionValidator
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.storage.sharedprefs.TracksStorage
import com.practicum.playlistmaker.search.data.storage.sharedprefs.SharedPrefsTracksStorage
import com.practicum.playlistmaker.settings.data.storage.sharedprefs.SettingsStorage
import com.practicum.playlistmaker.settings.data.storage.sharedprefs.SharedPrefsSettingsStorage
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://itunes.apple.com/"

val dataModule = module {
    
    single<ITunesApi> {
    
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(logging)
            .build()
    
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ITunesApi::class.java)
    }
    
    single {
        androidContext().getSharedPreferences(App.PREFERENCES, AppCompatActivity.MODE_PRIVATE)
    }
    
    single {
        Room
            .databaseBuilder(androidContext(), LocalDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    
    factoryOf(::TrackModelConverter)
    factoryOf(::PlaylistModelConverter)
    factoryOf(::InternetConnectionValidator)
    singleOf(::MediaPlayer)
    singleOf(::RetrofitNetworkClient).bind<NetworkClient>()
    singleOf(::SharedPrefsTracksStorage).bind<TracksStorage>()
    singleOf(::SharedPrefsSettingsStorage).bind<SettingsStorage>()
    singleOf(::AudioPlayerImpl).bind<AudioPlayer>()
    singleOf(::ExternalNavigatorImpl).bind<ExternalNavigator>()
}