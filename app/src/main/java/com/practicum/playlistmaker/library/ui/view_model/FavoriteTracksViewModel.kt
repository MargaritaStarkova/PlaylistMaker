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
    
    private val contentStateLiveData = MutableLiveData<ScreenState>()
    fun observeContentState(): LiveData<ScreenState> = contentStateLiveData
    
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
                contentStateLiveData.postValue(ScreenState.Empty)
            }
            
            else -> {
                contentStateLiveData.postValue(ScreenState.Content(trackList))
            }
        }
    }
}