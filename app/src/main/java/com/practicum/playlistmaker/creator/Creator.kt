package com.practicum.playlistmaker.creator

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.data.audioplayer.AudioPlayerImpl
import com.practicum.playlistmaker.data.network.NetworkClientImpl
import com.practicum.playlistmaker.data.network.SearchApi
import com.practicum.playlistmaker.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.data.storage.sharedprefs.SharedPrefsTracksStorage
import com.practicum.playlistmaker.domain.audioplayer.impl.MediaInteractorImpl
import com.practicum.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.practicum.playlistmaker.presentation.presenters.router.NavigationRouter
import com.practicum.playlistmaker.presentation.presenters.audioplayer.AudioPlayerPresenter
import com.practicum.playlistmaker.presentation.presenters.audioplayer.AudioPlayerView
import com.practicum.playlistmaker.presentation.presenters.search.SearchPresenter
import com.practicum.playlistmaker.presentation.presenters.search.SearchView

object Creator {

    fun provideAudioPlayerPresenter(
        view: AudioPlayerView,
        activity: AppCompatActivity
    ): AudioPlayerPresenter {
        val router = NavigationRouter(activity)
        val url = router.getTrackInfo().previewUrl
        return AudioPlayerPresenter(
            view = view,
            navigationRouter = router,
            mediaInteractor = MediaInteractorImpl(AudioPlayerImpl(url)),
        )
    }

    fun provideSearchPresenter(
        view: SearchView,
        sharedPrefs: SharedPreferences,
        api: SearchApi,
        router: NavigationRouter
    ): SearchPresenter {
        return SearchPresenter(
            view = view,
            searchInteractor = SearchInteractorImpl(
                repository = TrackRepositoryImpl(
                networkClient = NetworkClientImpl(api),
                tracksStorage = SharedPrefsTracksStorage(sharedPrefs)
            )),
            navigationRouter = router
        )
    }
}