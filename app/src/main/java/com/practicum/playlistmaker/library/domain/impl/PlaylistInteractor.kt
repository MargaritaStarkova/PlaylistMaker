package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.api.IPlaylistsInteractor
import com.practicum.playlistmaker.library.domain.api.IPlaylistsRepository
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

class PlaylistInteractor(
    private val repository: IPlaylistsRepository,
) : IPlaylistsInteractor {
    
    override fun getPlaylists(): Flow<List<PlaylistModel>> = repository.getSavedPlaylists()
    
    override fun isTrackAlreadyExists(playlist: PlaylistModel, track: TrackModel) =
        playlist.trackList.contains(track)
    
    override suspend fun addTrackToPlaylist(playlist: PlaylistModel, track: TrackModel) {
        playlist.trackList = playlist.trackList + track
        playlist.tracksCount = playlist.trackList.size
        repository.updateTracks(playlist)
    }
}