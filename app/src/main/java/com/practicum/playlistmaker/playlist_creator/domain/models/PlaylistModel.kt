package com.practicum.playlistmaker.playlist_creator.domain.models

import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistModel(
    val id: Int,
    var coverImageUrl: String,
    var playlistName: String,
    var playlistDescription: String,
    var trackList: List<TrackModel>,
    var tracksCount: Int,
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