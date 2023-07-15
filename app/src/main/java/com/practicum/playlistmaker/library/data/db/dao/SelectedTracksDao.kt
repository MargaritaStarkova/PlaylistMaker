package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)
    
    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM selected_tracks ORDER BY saveDate DESC;")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM selected_tracks WHERE id = :id LIMIT 1);")
    fun isFavorite(id: String): Flow<Boolean>
}