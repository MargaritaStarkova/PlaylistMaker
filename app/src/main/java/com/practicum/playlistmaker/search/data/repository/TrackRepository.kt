package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.network.INetworkClient
import com.practicum.playlistmaker.search.data.network.SearchResponse
import com.practicum.playlistmaker.search.data.storage.models.TrackModelDto
import com.practicum.playlistmaker.search.data.storage.sharedprefs.ITracksStorage
import com.practicum.playlistmaker.search.domain.api.ITrackRepository
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel

class TrackRepository(
    private val networkClient: INetworkClient,
    private val tracksStorage: ITracksStorage,
) : ITrackRepository {
    
    override fun loadTracks(
        query: String,
        onSuccess: (List<TrackModel>) -> Unit,
        onError: (NetworkError) -> Unit,
    ) {
    
        val response = networkClient.doRequest(query)
    
        when (response.resultCode) {
            
            -1 -> onError.invoke(NetworkError.CONNECTION_ERROR)
            in 400..499 -> {
                onError.invoke(NetworkError.SEARCH_ERROR)
            }
            in 500..599 -> onError.invoke(NetworkError.CONNECTION_ERROR)
        
            else -> {
                val resultList = (response as SearchResponse).results
            
                if (resultList.isNullOrEmpty()) {
                    onError.invoke(NetworkError.SEARCH_ERROR)
                } else {
                    val trackList = resultList.filter { it.previewUrl != null }
                    onSuccess.invoke(mapTrackListFromDto(trackList))
                }
            }
        }
    
    }

    override fun readHistory(): List<TrackModel> {
        return mapTrackListFromDto(tracksStorage.readHistory().toList())
    }

    override fun saveHistory(trackList: ArrayList<TrackModel>) {
        tracksStorage.saveHistory(mapTrackListToDto(trackList))
    }

    private fun mapTrackListFromDto(list: List<TrackModelDto>): List<TrackModel> {
    
        return list.map {
            TrackModel(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                artworkUrl100 = it.artworkUrl100,
                collectionName = it.collectionName,
                country = it.country,
                primaryGenreName = it.primaryGenreName,
                releaseDate = it.releaseDate,
                previewUrl = it.previewUrl!!,
            )
        }
    }

    private fun mapTrackListToDto(list: List<TrackModel>): List<TrackModelDto> {
    
        return list.map {
            TrackModelDto(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                artworkUrl100 = it.artworkUrl100,
                collectionName = it.collectionName,
                country = it.country,
                primaryGenreName = it.primaryGenreName,
                releaseDate = it.releaseDate,
                previewUrl = it.previewUrl,
            )
        }
    }
}

