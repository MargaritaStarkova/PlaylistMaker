package com.practicum.playlistmaker.search.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class TrackModel(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl60: String,
    val artworkUrl100: String,
    val collectionName: String,
    val country: String,
    val primaryGenreName: String,
    val releaseDate: String,
    val previewUrl: String,
) {
    
    val artworkUrl512: String = artworkUrl100.replaceAfterLast("/", "512x512bb.jpg")
    
    companion object {
        val emptyTrack = TrackModel(
            trackId = "",
            trackName = "",
            artistName = "",
            trackTimeMillis = 0,
            artworkUrl60 = "",
            artworkUrl100 = "",
            collectionName = "",
            country = "",
            primaryGenreName = "",
            releaseDate = "",
            previewUrl = "",
        )
    }
}
