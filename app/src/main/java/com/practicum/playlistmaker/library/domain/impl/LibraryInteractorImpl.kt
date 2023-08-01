package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.api.LibraryInteractor
import com.practicum.playlistmaker.library.domain.api.LibraryRepository
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow

class LibraryInteractorImpl(
    private val repository: LibraryRepository,
) : LibraryInteractor {
    
    override suspend fun likeTrack(track: TrackModel) {
        repository.saveTrack(track)
    }
    
    override suspend fun unLikeTrack(track: TrackModel) {
        repository.deleteTrack(track)
    }
    
    override fun getSelectedTracks(): Flow<List<TrackModel>> {
        return repository.getSelectedTracks()
    }
    
    override fun isFavorite(id: String): Flow<Boolean> {
        return repository.isFavorite(id)
    }
}