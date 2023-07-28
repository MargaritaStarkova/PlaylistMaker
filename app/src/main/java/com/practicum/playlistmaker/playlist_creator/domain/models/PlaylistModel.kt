package com.practicum.playlistmaker.playlist_creator.domain.models

import com.practicum.playlistmaker.search.domain.models.TrackModel

data class PlaylistModel(
    val id: Int,
    val coverImageUrl: String,
    val playlistName: String,
    val playlistDescription:String,
    val trackList: List<TrackModel>,
    val tracksCount: Int,
)