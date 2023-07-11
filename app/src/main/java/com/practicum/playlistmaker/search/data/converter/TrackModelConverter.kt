package com.practicum.playlistmaker.search.data.converter

import com.practicum.playlistmaker.search.data.storage.models.TrackModelDto
import com.practicum.playlistmaker.search.domain.models.TrackModel

class TrackModelConverter {
    
    fun mapDtoToModel(list: List<TrackModelDto>): List<TrackModel> = list.map {
        TrackModel(
            trackId = it.trackId ?: "",
            trackName = it.trackName ?: "",
            artistName = it.artistName ?: "",
            trackTimeMillis = it.trackTimeMillis ?: 0,
            artworkUrl100 = it.artworkUrl100 ?: "",
            collectionName = it.collectionName ?: "",
            country = it.country ?: "",
            primaryGenreName = it.primaryGenreName ?: "",
            releaseDate = it.releaseDate ?: "",
            previewUrl = it.previewUrl ?: "",
        )
    }
    
     fun mapModelToDto(list: List<TrackModel>): List<TrackModelDto> = list.map {
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