package com.practicum.playlistmaker.library.data.db.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Keep
@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "playlist_name")
    val playlistName: String,
    @ColumnInfo(name = "playlist_description")
    val playlistDescription: String,
    @ColumnInfo(name = "cover_path")
    val imageUrl: String,
    val trackList: String,
    val countTracks: Int,
    val saveDate: Date,
)