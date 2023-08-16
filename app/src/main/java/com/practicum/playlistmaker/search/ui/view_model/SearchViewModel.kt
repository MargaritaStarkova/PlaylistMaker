package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.utils.debounce
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.FetchResult
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.models.SearchContentState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val interactor: SearchInteractor,
) : ViewModel() {
    
    private val contentState = MutableLiveData<SearchContentState>()
    private val clearIconState = MutableLiveData<String>()
    private val searchTextClearClicked = SingleLiveEvent<Boolean>()
    
    private var latestSearchText: String? = null
    
    private var searchJob: Job? = null
    
    private val onSearchDebounce = debounce<String>(delayMillis = SEARCH_DEBOUNCE_DELAY,
        coroutineScope = viewModelScope,
        useLastParam = true,
        action = { query -> loadTrackList(query) })
    
    init {
        contentState.value = SearchContentState.HistoryContent(interactor.historyList)
    }
    
    fun observeContentState(): LiveData<SearchContentState> = contentState
    fun observeClearIconState(): LiveData<String> = clearIconState
    fun observeSearchTextClearClicked(): LiveData<Boolean> = searchTextClearClicked
    
    fun onHistoryClearedClicked() {
        interactor.historyListCleared()
        contentState.value = SearchContentState.HistoryContent(interactor.historyList)
    }
    
    fun searchFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            contentState.value = SearchContentState.HistoryContent(interactor.historyList)
        }
    }

    fun loadTrackList(query: String) {
        if (query.isEmpty()) {
            return
        }
        contentState.value = SearchContentState.Loading
    
        searchJob = viewModelScope.launch {
            interactor
                .getTracksOnQuery(query = query)
                .collect { result ->
                    processResult(result)
                }
        }
    }
    
    fun searchTextClearClicked() {
        onSearchDebounce("")
        searchJob?.cancel()
        searchTextClearClicked.value = true
        contentState.value = SearchContentState.HistoryContent(interactor.historyList)
    }
    
    fun onSearchTextChanged(query: String?) {
    
        clearIconState.value = query ?: ""
    
        if (query.isNullOrEmpty()) {
            contentState.value = SearchContentState.HistoryContent(interactor.historyList)
        } else {
    
            if (latestSearchText == query) return
    
            latestSearchText = query
            onSearchDebounce(query)
        }
    }
    
    private fun processResult(result: FetchResult) {
        when {
            result.error != null -> {
                contentState.value = SearchContentState.Error(result.error)
            }
    
            result.data != null -> {
                contentState.value = SearchContentState.SearchContent(result.data)
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
