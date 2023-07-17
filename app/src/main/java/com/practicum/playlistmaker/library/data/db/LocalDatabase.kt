package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.practicum.playlistmaker.library.data.db.dao.SelectedTracksDao
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.library.data.db.entity.TypeConverter

@Database(version = 1, entities = [TrackEntity::class])
@TypeConverters(TypeConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    
    abstract fun dao(): SelectedTracksDao
}