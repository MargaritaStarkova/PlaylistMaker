package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.library.data.converter.TrackModelConverter
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.SearchResponse
import com.practicum.playlistmaker.search.data.storage.sharedprefs.TracksStorage
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.models.FetchResult
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksStorage: TracksStorage,
    private val converter: TrackModelConverter,
) : TrackRepository {
    
    override fun loadTracks(query: String): Flow<FetchResult> = flow {
        
        val response = networkClient.doRequest(query)
        
        when (response.resultCode) {
            
            in 100..399 -> {
                val resultList = (response as SearchResponse).results
                if (resultList.isEmpty()) {
                    emit(FetchResult.Error(NetworkError.SEARCH_ERROR))
                } else {
                    val trackList = resultList.filter { it.previewUrl != null }
                    emit(FetchResult.Success(trackList.map { converter.map(it) }))
                }
            }
            
            in 400..499 -> {
                emit(FetchResult.Error(NetworkError.SEARCH_ERROR))
            }
            
            else -> {
                emit(FetchResult.Error(NetworkError.CONNECTION_ERROR))
            }
        }
    }
    
    override fun readHistory(): List<TrackModel> {
        return tracksStorage.readHistory().map { converter.map(it) }
    }
    
    override fun saveHistory(trackList: ArrayList<TrackModel>) {
        tracksStorage.saveHistory(trackList.map { converter.map(it) })
    }
}

