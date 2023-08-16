package com.practicum.playlistmaker.library.data.db.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Keep
@Entity(tableName = "selected_tracks")
data class TrackEntity(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = false)
    val trackId: String,
    val trackName: String,
    val artistName: String,
    @ColumnInfo(name = "track_duration")
    val trackTimeMillis: Int,
    @ColumnInfo(name = "cover_url_60x60")
    val artworkUrl60: String,
    @ColumnInfo(name = "cover_url_100x100")
    val artworkUrl100: String,
    val collectionName: String,
    val country: String,
    @ColumnInfo(name = "genre")
    val primaryGenreName: String,
    val releaseDate: String,
    val previewUrl: String,
    val saveDate: Date,
)
