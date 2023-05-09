package com.practicum.playlistmaker.creator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.application.App
import com.practicum.playlistmaker.player.data.audioplayer.AudioPlayerImpl
import com.practicum.playlistmaker.player.domain.impl.MediaInteractorImpl
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerPresenter
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerView
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.ISearchApi
import com.practicum.playlistmaker.search.data.repository.TrackRepository
import com.practicum.playlistmaker.search.data.storage.sharedprefs.SharedPrefsTracksStorage
import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.api.ITrackRepository
import com.practicum.playlistmaker.search.domain.impl.SearchInteractor
import com.practicum.playlistmaker.utils.router.NavigationRouter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private const val BASE_URL = "http://itunes.apple.com/"

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()

    private val api = retrofit.create(ISearchApi::class.java)



    fun provideAudioPlayerPresenter(
        view: AudioPlayerView, activity: AppCompatActivity
    ): AudioPlayerPresenter {
        val router = NavigationRouter(activity)
        val url = router.getTrackInfo().previewUrl
        return AudioPlayerPresenter(
            view = view,
            navigationRouter = router,
            mediaInteractor = MediaInteractorImpl(AudioPlayerImpl(url)),
        )
    }

    fun provideSearchInteractor(context: Context): ISearchInteractor {
        return SearchInteractor(
            repository = provideTrackRepository(context)
        )
    }

    private fun provideTrackRepository(context: Context): ITrackRepository {
        return TrackRepository(
            networkClient = NetworkClient(api),
            tracksStorage = provideTracksStorage(context),
            )
    }

    private fun provideTracksStorage(context: Context): SharedPrefsTracksStorage {
        return SharedPrefsTracksStorage(context.getSharedPreferences(App.PREFERENCES, AppCompatActivity.MODE_PRIVATE))
    }

}