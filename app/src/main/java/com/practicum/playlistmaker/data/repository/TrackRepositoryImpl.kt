package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.data.storage.models.TrackModelDto
import com.practicum.playlistmaker.data.storage.sharedprefs.TracksStorage
import com.practicum.playlistmaker.domain.models.TrackModel
import com.practicum.playlistmaker.domain.models.NetworkError
import com.practicum.playlistmaker.domain.search.api.TrackRepository

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksStorage: TracksStorage
) : TrackRepository {

    override fun loadTracks(
        query: String,
        onSuccess: (List<TrackModel>) -> Unit,
        onError: (NetworkError) -> Unit
    ) {
        networkClient.doRequest(
            query = query,
            onSuccess = { onSuccess.invoke(mapTrackListFromDto(it)) },
            onError = onError
        )
    }

    override fun readHistory(): List<TrackModel> {
        return mapTrackListFromDto(tracksStorage.readHistory().toList())
    }

    override fun saveHistory(trackList: ArrayList<TrackModel>) {
        tracksStorage.saveHistory(mapTrackListToDto(trackList))
    }

    private fun mapTrackListFromDto(list: List<TrackModelDto>): List<TrackModel> {

        fun mapping(track: TrackModelDto) =  TrackModel(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                collectionName = track.collectionName,
                country = track.country,
                primaryGenreName = track.primaryGenreName,
                releaseDate = track.releaseDate,
                previewUrl = track.previewUrl!!,
            )

        return list.map{ mapping(it) }
    }

    private fun mapTrackListToDto(list: List<TrackModel>): List<TrackModelDto> {

        fun mapping(track: TrackModel) =  TrackModelDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            previewUrl = track.previewUrl,
        )

        return list.map{ mapping(it) }
    }
}

