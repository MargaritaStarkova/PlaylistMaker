package com.practicum.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.ILibraryInteractor
import com.practicum.playlistmaker.library.ui.models.FavoriteState
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val interactor: ILibraryInteractor,
) : ViewModel() {
    
    init {
        fillData()
    }
    
    private val contentStateLiveData = MutableLiveData<FavoriteState>()
    fun observeContentState(): LiveData<FavoriteState> = contentStateLiveData
    
    private fun fillData() {
        viewModelScope.launch {
            interactor
                .getSelectedTracks()
                .collect { trackList ->
                    processResult(trackList)
                }
        }
    }
    
    private fun processResult(trackList: List<TrackModel>) {
        
        when {
            trackList.isEmpty() -> {
                contentStateLiveData.value = FavoriteState.Empty
            }
            
            else -> {
                contentStateLiveData.value = FavoriteState.SelectedTracks(trackList)
            }
        }
    }
}