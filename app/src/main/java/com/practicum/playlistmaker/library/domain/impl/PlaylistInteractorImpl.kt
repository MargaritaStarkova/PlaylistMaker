package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistsRepository,
) : PlaylistsInteractor {
    
    override fun getPlaylists(): Flow<List<PlaylistModel>> {
        return repository.getSavedPlaylists()
    }
    
    override fun isTrackAlreadyExists(playlist: PlaylistModel, track: TrackModel): Boolean {
        return playlist.trackList.contains(track)
    }
    
    override suspend fun addTrackToPlaylist(playlist: PlaylistModel, track: TrackModel) {
        playlist.trackList = listOf(track) + playlist.trackList
        playlist.tracksCount = playlist.trackList.size
        repository.updateTracks(playlist)
    }
    
    override suspend fun deleteTrack(playlist: PlaylistModel, track: TrackModel): PlaylistModel {
        playlist.trackList = playlist.trackList - track
        playlist.tracksCount = playlist.trackList.size
        repository.updateTracks(playlist)
        
        return playlist
    }
    
    override suspend fun deletePlaylist(playlist: PlaylistModel) {
        repository.deletePlaylist(playlist)
    }
    
    override fun getPlaylist(id: Int): Flow<PlaylistModel> {
        return repository.getPlaylistById(id)
    }
}