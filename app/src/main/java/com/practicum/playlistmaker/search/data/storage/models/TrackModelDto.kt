package com.practicum.playlistmaker.search.data.storage.models

data class TrackModelDto(

    val trackId: String?,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Int?,
    val artworkUrl60: String?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val country: String?,
    val primaryGenreName: String?,
    val releaseDate: String?,
    val previewUrl: String?,
)

