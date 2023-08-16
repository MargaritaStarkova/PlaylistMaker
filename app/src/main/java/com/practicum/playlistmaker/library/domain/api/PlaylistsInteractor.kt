package com.practicum.playlistmaker.library.domain.api

import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    
    fun getPlaylists(): Flow<List<PlaylistModel>>
    fun isTrackAlreadyExists(playlist: PlaylistModel, track: TrackModel): Boolean
    suspend fun addTrackToPlaylist(playlist: PlaylistModel, track: TrackModel)
    suspend fun deleteTrack(playlist: PlaylistModel, track: TrackModel): PlaylistModel
    suspend fun deletePlaylist(playlist: PlaylistModel)
    fun getPlaylist(id: Int): Flow<PlaylistModel>
}