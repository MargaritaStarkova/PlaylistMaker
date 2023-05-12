package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.api.ITrackRepository
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import java.util.concurrent.Executors

class SearchInteractor(private val repository: ITrackRepository) : ISearchInteractor {
    
    private val executor = Executors.newCachedThreadPool()
    
    override fun getTracksOnQuery(
        query: String,
        onSuccess: (List<TrackModel>) -> Unit,
        onError: (NetworkError) -> Unit,
    ) {
        executor.execute {
            repository.loadTracks(
                query = query, onSuccess = onSuccess, onError = onError
            )
        }
    }
    override fun saveSearchHistory(trackList: ArrayList<TrackModel>) {
        repository.saveHistory(trackList)
    }
    override fun getTracksFromHistory(): List<TrackModel> {
        return repository.readHistory()
    }
}