package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.domain.api.ISearchInteractor
import com.practicum.playlistmaker.search.domain.api.ITrackRepository

class SearchInteractor(private val repository: ITrackRepository) : ISearchInteractor {

    override fun getTracksOnQuery(
        query: String,
        onSuccess: (List<TrackModel>) -> Unit,
        onError: (NetworkError) -> Unit) {

        repository.loadTracks(query = query, onSuccess = onSuccess, onError = onError)
    }

    override fun saveSearchHistory(trackList: ArrayList<TrackModel>) {
        repository.saveHistory(trackList)
    }

    override fun getTracksFromHistory(): List<TrackModel> {
        return repository.readHistory()
    }
}