package com.practicum.playlistmaker.search.domain.models

data class TrackModel(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val collectionName: String,
    val country: String,
    val primaryGenreName: String,
    val releaseDate: String,
    val previewUrl: String,
)
