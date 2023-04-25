package com.practicum.playlistmaker.presentation.presenters.search

import com.practicum.playlistmaker.domain.models.TrackModel
import com.practicum.playlistmaker.domain.search.api.SearchInteractor
import com.practicum.playlistmaker.domain.models.NetworkError
import com.practicum.playlistmaker.presentation.presenters.router.HandlerRouter
import com.practicum.playlistmaker.presentation.presenters.router.NavigationRouter

class SearchPresenter(
    private val view: SearchView,
    private val searchInteractor: SearchInteractor,
    private val navigationRouter: NavigationRouter,
    ) {

    private val historyList = ArrayList<TrackModel>()
    private val handlerRouter = HandlerRouter()

    init {
        historyList.addAll(searchInteractor.getTracksFromHistory())
        view.showHistoryList(historyList)
    }

    fun onHistoryClearedClicked() {
        historyList.clear()
        view.showHistoryList(historyList)
    }

    fun searchFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            view.showHistoryList(historyList)
        }
    }

    fun loadTrackList(query: String) {
        if (query.isEmpty()) {
            return
        }
        view.showLoading()
        searchInteractor.getTracksOnQuery(query = query,
            onSuccess = { trackList ->
            if (trackList.isEmpty()) {
                view.showMessage(NetworkError.SEARCH_ERROR)
            } else {
                view.showSearchList(trackList)
            }
        }, onError = { error ->
            when (error) {
                NetworkError.SEARCH_ERROR -> view.showMessage(error)
                NetworkError.CONNECTION_ERROR -> view.showMessage(error)
            }
        })
    }

    fun searchTextClearClicked() {
        view.clearSearchText()
        view.hideKeyboard()
        view.showHistoryList(historyList)
    }

    fun backButtonClicked() {
        navigationRouter.goBack()
    }

    fun onTrackClicked(track: TrackModel) {
        if (handlerRouter.clickDebounce()) {
            addTrackToHistoryList(track)
            navigationRouter.openAudioPlayer(track)
        }
    }

    fun onViewStopped() {
        searchInteractor.saveSearchHistory(historyList)
    }

    fun onViewDestroyed() {
        handlerRouter.stopRunnable()
    }

    fun onSearchTextChanged(query: String?) {
        view.clearButtonVisibility(query)

        if (query.isNullOrEmpty()) {
            view.showHistoryList(historyList)
        } else {

            handlerRouter.searchDebounce(r = { loadTrackList(query) })
        }
    }

    private fun addTrackToHistoryList(track: TrackModel) {
        when {
            historyList.contains(track) -> {
                historyList.remove(track)
                historyList.add(0, track)
            }

            historyList.size < 10 -> {
                historyList.add(0, track)
            }

            else -> {
                historyList.removeAt(9)
                historyList.add(0, track)
            }
        }
    }
}