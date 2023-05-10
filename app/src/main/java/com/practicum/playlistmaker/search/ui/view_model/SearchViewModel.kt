package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.application.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.models.SearchContentState
import com.practicum.playlistmaker.utils.router.HandlerRouter

class SearchViewModel(
    private val searchInteractor: ISearchInteractor,
    private val handlerRouter: HandlerRouter,
) : ViewModel() {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as App
                SearchViewModel(
                    searchInteractor = Creator.provideSearchInteractor(context = application),
                    handlerRouter = HandlerRouter(),
                )
            }
        }
    }
    private val historyList = ArrayList<TrackModel>()
    
    private val contentStateLiveData = MutableLiveData<SearchContentState>()
    private val clearIconStateLiveData = MutableLiveData<String>()
    private val searchTextClearClickedLiveData = MutableLiveData(false)
    
    private var latestStateLiveData = contentStateLiveData.value

    init {
        historyList.addAll(searchInteractor.getTracksFromHistory())
        contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
        latestStateLiveData = contentStateLiveData.value
    }

    override fun onCleared() {
        super.onCleared()
        handlerRouter.stopRunnable()
    }

    fun observeContentState(): LiveData<SearchContentState> = contentStateLiveData
    fun observeClearIconState(): LiveData<String> = clearIconStateLiveData
    fun observeSearchTextClearClicked(): LiveData<Boolean> = searchTextClearClickedLiveData
    
    fun onViewResume() {
        contentStateLiveData.value =
            latestStateLiveData
    }

    fun onHistoryClearedClicked() {
        historyList.clear()
        searchInteractor.saveSearchHistory(historyList)
        contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
        latestStateLiveData = contentStateLiveData.value
    }

    fun searchFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
            latestStateLiveData = contentStateLiveData.value
        }
    }

    fun loadTrackList(query: String) {
        if (query.isEmpty()) {
            return
        }
        contentStateLiveData.value = SearchContentState.Loading
        searchInteractor.getTracksOnQuery(
            query = query,
            onSuccess = { trackList ->
                contentStateLiveData.postValue(SearchContentState.SearchContent(trackList))
                latestStateLiveData = SearchContentState.SearchContent(trackList)
    
            }, onError = { error ->
                contentStateLiveData.postValue(SearchContentState.Error(error))
            })
    }

    fun searchTextClearClicked() {
        searchTextClearClickedLiveData.value = true
        contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
        latestStateLiveData = contentStateLiveData.value
    }

    fun onSearchTextChanged(query: String?) {
        
        clearIconStateLiveData.value = query

        if (query.isNullOrEmpty()) {
            contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
            latestStateLiveData = contentStateLiveData.value
        } else {
            handlerRouter.searchDebounce(r = { loadTrackList(query) })
        }
    }

    fun addTrackToHistoryList(track: TrackModel) {
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
        searchInteractor.saveSearchHistory(historyList)
    }
}
