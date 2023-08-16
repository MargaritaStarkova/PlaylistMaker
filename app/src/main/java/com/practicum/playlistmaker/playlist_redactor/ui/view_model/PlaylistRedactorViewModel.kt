package com.practicum.playlistmaker.playlist_redactor.ui.view_model

import com.practicum.playlistmaker.playlist_creator.domain.api.CreatePlaylistUseCase
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import com.practicum.playlistmaker.playlist_creator.ui.view_model.PlaylistCreatorViewModel
import java.net.URI

class PlaylistRedactorViewModel(useCase: CreatePlaylistUseCase) :
    PlaylistCreatorViewModel(useCase) {
    
    private var playlist: PlaylistModel? = null
    
    override fun onPlaylistNameChanged(playlistName: String?) {
        if (playlistName != null) {
            playlist = playlist?.copy(playlistName = playlistName)
        }
        super.onPlaylistNameChanged(playlistName)
    }
    
    override fun onPlaylistDescriptionChanged(playlistDescription: String?) {
        
        if (playlistDescription != null) {
            playlist = playlist?.copy(playlistDescription = playlistDescription)
        }
    }
    
    override fun saveImageUri(uri: URI) {
        playlist = playlist?.copy(coverImageUrl = uri.toString())
    }
    
    override fun createPlaylist(): PlaylistModel {
        return this.playlist ?: PlaylistModel.emptyPlaylist
    }
    
    fun initPlaylist(playlist: PlaylistModel) {
        this.playlist = playlist
    }
}