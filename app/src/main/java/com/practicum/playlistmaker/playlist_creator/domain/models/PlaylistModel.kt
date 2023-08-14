package com.practicum.playlistmaker.playlist_creator.domain.models

import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistModel(
    val id: Int,
    val coverImageUrl: String,
    val playlistName: String,
    val playlistDescription: String,
    val trackList: List<TrackModel>,
    val tracksCount: Int,
) {
    companion object {
        val emptyPlaylist = PlaylistModel(
            id = 0,
            coverImageUrl = "",
            playlistName = "",
            playlistDescription = "",
            trackList = emptyList(),
            tracksCount = 0,
        )
    }
}