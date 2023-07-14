package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.api.ILibraryInteractor
import com.practicum.playlistmaker.library.domain.api.ILibraryRepository
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

class LibraryInteractor(
    private val repository: ILibraryRepository,
) : ILibraryInteractor {
    
    override fun likeTrack(track: TrackModel) {
        repository.saveTrack(track)
    }
    
    override fun unLikeTrack(track: TrackModel) {
        repository.deleteTrack(track)
    }
    
    override fun getSelectedTracks(): Flow<List<TrackModel>> = repository.getSelectedTracks()
    
    override fun isFavorite(id: String): Boolean = repository.isFavorite(id)
}