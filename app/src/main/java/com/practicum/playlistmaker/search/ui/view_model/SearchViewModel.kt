package com.practicum.playlistmaker.search.ui.view_model

import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.practicum.playlistmaker.application.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.models.SearchContentState
import com.practicum.playlistmaker.utils.router.HandlerRouter

class SearchViewModel(
    application: App,
    private val searchInteractor: SearchInteractor,
    private val handlerRouter: HandlerRouter,
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as App
                SearchViewModel(
                    application = application,
                    searchInteractor = Creator.provideSearchInteractor(context = application),
                    handlerRouter = HandlerRouter()
                )
            }
        }
    }

    private val historyList = ArrayList<TrackModel>()
    private val contentStateLiveData = MutableLiveData<SearchContentState>()
    private val clearIconStateLiveData = MutableLiveData<String>()
    private val searchTextClearClickedLiveData = MutableLiveData(false)

    init {
        historyList.addAll(searchInteractor.getTracksFromHistory())
        contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
    }

    override fun onCleared() {
        super.onCleared()
        handlerRouter.stopRunnable()
    }

    fun observeContentState(): LiveData<SearchContentState> = contentStateLiveData
    fun observeClearIconState(): LiveData<String> = clearIconStateLiveData
    fun observeSearchTextClearClicked(): LiveData<Boolean> = searchTextClearClickedLiveData

    fun onHistoryClearedClicked() {
        historyList.clear()
        contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
    }

    fun searchFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
        }
    }

    fun loadTrackList(query: String) {
        if (query.isEmpty()) {
            return
        }
        contentStateLiveData.value = SearchContentState.Loading
        searchInteractor.getTracksOnQuery(query = query,
            onSuccess = { trackList ->
                if (trackList.isEmpty()) {
                    contentStateLiveData.value = SearchContentState.Error(NetworkError.SEARCH_ERROR)
                } else {
                    contentStateLiveData.value = SearchContentState.SearchContent(trackList)
                }
            }, onError = { error ->
                SearchContentState.Error(error)
            })
    }

    fun searchTextClearClicked() {
        searchTextClearClickedLiveData.value = true
        SearchContentState.HistoryContent(historyList)
    }

    fun onTrackClicked(track: TrackModel) {
        if (handlerRouter.clickDebounce()) {
            addTrackToHistoryList(track)
            //navigationRouter.openAudioPlayer(track)
            Toast.makeText(getApplication(), "КЛИКНУЛИ НА ТРЕК", Toast.LENGTH_SHORT).show()
        }
    }

    fun onViewStopped() {
        searchInteractor.saveSearchHistory(historyList)
    }

    fun onSearchTextChanged(query: String?) {

        clearIconStateLiveData.value = query

        if (query.isNullOrEmpty()) {
            contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
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
