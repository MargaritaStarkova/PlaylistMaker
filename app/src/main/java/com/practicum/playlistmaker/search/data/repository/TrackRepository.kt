package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.network.INetworkClient
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
        networkClient.doRequest(
            query = query,
            onSuccess = { onSuccess.invoke(mapTrackListFromDto(it)) },
            onError = onError,
        )
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

