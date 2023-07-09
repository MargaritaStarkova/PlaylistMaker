package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.utils.debounce
import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.models.FetchResult
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.models.SearchContentState
import kotlinx.coroutines.launch

class SearchViewModel(
    private val interactor: ISearchInteractor,
) : ViewModel() {
    
    private val contentStateLiveData = MutableLiveData<SearchContentState>()
    private val clearIconStateLiveData = MutableLiveData<String>()
    private val searchTextClearClickedLiveData = MutableLiveData(false)
    
    private var latestStateContent = contentStateLiveData.value
    private var latestSearchText: String? = null
    
    private val onSearchDebounce = debounce<String>(delayMillis = SEARCH_DEBOUNCE_DELAY,
        coroutineScope = viewModelScope,
        useLastParam = true,
        action = { query -> loadTrackList(query) })
    
    init {
        contentStateLiveData.value = SearchContentState.HistoryContent(interactor.historyList)
        latestStateContent = contentStateLiveData.value
    }
    
    fun observeContentState(): LiveData<SearchContentState> = contentStateLiveData
    fun observeClearIconState(): LiveData<String> = clearIconStateLiveData
    fun observeSearchTextClearClicked(): LiveData<Boolean> = searchTextClearClickedLiveData
    
    fun onViewResume() {
        contentStateLiveData.value = latestStateContent!!
    }
    
    fun onHistoryClearedClicked() {
        interactor.historyListCleared()
        contentStateLiveData.value = SearchContentState.HistoryContent(interactor.historyList)
        latestStateContent = contentStateLiveData.value
    }
    
    fun searchFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            contentStateLiveData.value = SearchContentState.HistoryContent(interactor.historyList)
            latestStateContent = contentStateLiveData.value
        }
    }

    fun loadTrackList(query: String) {
        if (query.isEmpty()) {
            return
        }
        contentStateLiveData.value = SearchContentState.Loading
    
        viewModelScope.launch {
            interactor
                .getTracksOnQuery(query = query)
                .collect { result ->
                    processResult(result)
                }
        }
    }
    
    fun searchTextClearClicked() {
        searchTextClearClickedLiveData.value = true
        contentStateLiveData.value = SearchContentState.HistoryContent(interactor.historyList)
        latestStateContent = contentStateLiveData.value
    }
    
    fun onSearchTextChanged(query: String?) {
    
        clearIconStateLiveData.value = query ?: ""
    
        if (query.isNullOrEmpty()) {
            contentStateLiveData.value = SearchContentState.HistoryContent(interactor.historyList)
            latestStateContent = contentStateLiveData.value
        } else {
    
            if (latestSearchText == query) return
    
            latestSearchText = query
            onSearchDebounce(query)
        }
    }
    
    private fun processResult(result: FetchResult) {
        when {
            result.error != null -> {
                contentStateLiveData.postValue(SearchContentState.Error(result.error))
            }
    
            result.data != null -> {
                contentStateLiveData.postValue(SearchContentState.SearchContent(result.data))
                latestStateContent = SearchContentState.SearchContent(result.data)
            }
        }
    }
    
    fun addTrackToHistoryList(track: TrackModel) {
        interactor.addTrackToHistoryList(track)
    }
    
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
