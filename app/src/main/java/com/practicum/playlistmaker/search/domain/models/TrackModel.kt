package com.practicum.playlistmaker.search.domain.models

import kotlinx.serialization.Serializable

@Serializable
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
) {
    companion object {
        val emptyTrack = TrackModel(
            trackId = "",
            trackName = "",
            artistName = "",
            trackTimeMillis = 0,
            artworkUrl100 = "",
            collectionName = "",
            country = "",
            primaryGenreName = "",
            releaseDate = "",
            previewUrl = "",
        )
    }
}
