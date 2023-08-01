package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.models.FetchResult
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

class SearchInteractorImpl(private val repository: TrackRepository) : SearchInteractor {
    
    override val historyList = ArrayList<TrackModel>(getTracksFromHistory())
    
    override fun getTracksOnQuery(query: String): Flow<FetchResult> {
        return repository.loadTracks(query = query)
    }
    
    override fun addTrackToHistoryList(track: TrackModel) {
        when {
            historyList.size < 10 || historyList.contains(track) -> {
                historyList.remove(track)
                historyList.add(FIRST_INDEX_HISTORY_LIST, track)
            }
            
            else -> {
                historyList.removeAt(LAST_INDEX_HISTORY_LIST)
                historyList.add(FIRST_INDEX_HISTORY_LIST, track)
            }
        }
        saveSearchHistory(historyList)
    }
    
    override fun historyListCleared() {
        historyList.clear()
        saveSearchHistory(historyList)
    }
    
    private fun saveSearchHistory(trackList: ArrayList<TrackModel>) {
        repository.saveHistory(trackList)
    }
    
    private fun getTracksFromHistory(): List<TrackModel> {
        return repository.readHistory()
    }
    
    companion object {
        private const val FIRST_INDEX_HISTORY_LIST = 0
        private const val LAST_INDEX_HISTORY_LIST = 9
    }
}