package com.practicum.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.LibraryInteractor
import com.practicum.playlistmaker.library.ui.models.ScreenState
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val interactor: LibraryInteractor,
) : ViewModel() {
    
    init {
        fillData()
    }
    
    private val _contentState = MutableLiveData<ScreenState>()
    val contentState: LiveData<ScreenState> = _contentState
    
    private fun fillData() {
        viewModelScope.launch(Dispatchers.IO) {
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
                _contentState.postValue(ScreenState.Empty)
            }
            
            else -> {
                _contentState.postValue(ScreenState.Content(trackList))
            }
        }
    }
}