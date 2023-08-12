package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.practicum.playlistmaker.library.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.library.data.db.dao.SelectedTracksDao
import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.library.data.db.entity.TypeConverter

@Database(version = 7, entities = [TrackEntity::class, PlaylistEntity::class])
@TypeConverters(TypeConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    
    abstract fun selectedTracksDao(): SelectedTracksDao
    abstract fun playlistsDao(): PlaylistDao
}