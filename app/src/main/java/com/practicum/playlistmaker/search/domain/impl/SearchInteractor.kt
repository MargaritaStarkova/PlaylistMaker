package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.api.ITrackRepository
import com.practicum.playlistmaker.search.domain.models.FetchResult
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractor(private val repository: ITrackRepository) : ISearchInteractor {
    
    override val historyList = ArrayList<TrackModel>(getTracksFromHistory())
    
    override fun getTracksOnQuery(query: String): Flow<Pair<List<TrackModel>?, NetworkError?>> {
        return repository
            .loadTracks(query = query)
            .map { fetchResult ->
                when (fetchResult) {
                    is FetchResult.Success -> {
                        Pair(fetchResult.data, null)
                    }
                    
                    is FetchResult.Error -> {
                        Pair(null, fetchResult.error)
                    }
                }
            }
    }
    
    override fun addTrackToHistoryList(track: TrackModel) {
        when {
            historyList.size < 10 -> {
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