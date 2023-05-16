package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.models.SearchContentState
import com.practicum.playlistmaker.utils.router.HandlerRouter

class SearchViewModel(
    private val searchInteractor: ISearchInteractor,
    private val handlerRouter: HandlerRouter,
) : ViewModel() {
    
    private val historyList = ArrayList<TrackModel>()
    
    private val contentStateLiveData = MutableLiveData<SearchContentState>()
    private val clearIconStateLiveData = MutableLiveData<String>()
    private val searchTextClearClickedLiveData = MutableLiveData(false)
    
    private var latestStateContent = contentStateLiveData.value

    init {
        historyList.addAll(searchInteractor.getTracksFromHistory())
        contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
        latestStateContent = contentStateLiveData.value
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
            latestStateContent!!
    }

    fun onHistoryClearedClicked() {
        historyList.clear()
        searchInteractor.saveSearchHistory(historyList)
        contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
        latestStateContent = contentStateLiveData.value
    }

    fun searchFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
            latestStateContent = contentStateLiveData.value
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
                latestStateContent = SearchContentState.SearchContent(trackList)
    
            }, onError = { error ->
                contentStateLiveData.postValue(SearchContentState.Error(error))
            })
    }

    fun searchTextClearClicked() {
        searchTextClearClickedLiveData.value = true
        contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
        latestStateContent = contentStateLiveData.value
    }

    fun onSearchTextChanged(query: String?) {
    
        clearIconStateLiveData.value = query ?: ""

        if (query.isNullOrEmpty()) {
            contentStateLiveData.value = SearchContentState.HistoryContent(historyList)
            latestStateContent = contentStateLiveData.value
        } else {
            handlerRouter.searchDebounce(r = { loadTrackList(query) })
        }
    }

    fun addTrackToHistoryList(track: TrackModel) {
        when {
            historyList.contains(track) -> {
                historyList.remove(track)
                historyList.add(FIRST_INDEX_HISTORY_LIST, track)

            }

            historyList.size < 10 -> {
                historyList.add(FIRST_INDEX_HISTORY_LIST, track)
            }

            else -> {
                historyList.removeAt(LAST_INDEX_HISTORY_LIST)
                historyList.add(FIRST_INDEX_HISTORY_LIST, track)
            }
        }
        searchInteractor.saveSearchHistory(historyList)
    }
    
    companion object {
        private const val FIRST_INDEX_HISTORY_LIST = 0
        private const val LAST_INDEX_HISTORY_LIST = 9
    }
}
