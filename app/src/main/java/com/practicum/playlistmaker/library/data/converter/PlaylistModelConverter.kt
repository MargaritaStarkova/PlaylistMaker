package com.practicum.playlistmaker.library.data.converter

import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

class PlaylistModelConverter {
    
    fun map(playlist: PlaylistModel): PlaylistEntity {
        return with(playlist) {
            PlaylistEntity(
                id = id,
                playlistName = playlistName,
                playlistDescription = playlistDescription,
                imageUrl = coverImageUrl,
                trackList = Json.encodeToString(trackList),
                countTracks = tracksCount,
                saveDate = Date(),
            )
        }
    }
    
    fun map(playlist: PlaylistEntity): PlaylistModel {
        return with(playlist) {
            PlaylistModel(
                id = id,
                playlistName = playlistName,
                playlistDescription = playlistDescription,
                coverImageUrl = imageUrl,
                trackList = Json.decodeFromString(trackList),
                tracksCount = countTracks,
            )
        }
    }
}