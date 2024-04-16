package com.practicum.playlistmaker.player.ui.view_model

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import com.practicum.playlistmaker.library.domain.api.LibraryInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerControl
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.playlist_creator.domain.models.PermissionResultState
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val libraryInteractor: LibraryInteractor,
) : ViewModel() {

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default())
    private val permissionState = Channel<PermissionResultState>()
    private val _isFavoriteStatus = MutableLiveData<Boolean>()
    private val register = PermissionRequester.instance()
    private var playerState: PlayerState
        get() = _playerState.value
        set(value) {
            _playerState.value = value
        }

    val isFavoriteStatus: LiveData<Boolean> = _isFavoriteStatus

    private var playerControl: PlayerControl? = null
    private var isFavorite: Boolean = false

    override fun onCleared() {
        playerControl?.pausePlayer()
        playerControl = null
        super.onCleared()
    }

    fun observePlayerState() = _playerState.asStateFlow()
    fun observePermissionState() = permissionState.receiveAsFlow()

    fun isFavorite(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            libraryInteractor
                .isFavorite(id)
                .collect {
                    isFavorite = it
                    _isFavoriteStatus.postValue(isFavorite)
                }
        }
    }

    fun toggleFavorite(track: TrackModel) {
        isFavorite = !isFavorite
        _isFavoriteStatus.value = isFavorite
        viewModelScope.launch(Dispatchers.IO) {
            if (isFavorite) {
                libraryInteractor.likeTrack(track = track)
            } else {
                libraryInteractor.unLikeTrack(track = track)
            }
        }
    }

    fun initPlayerControl(playerControl: PlayerControl) {
        this.playerControl = playerControl

        playerControl.observePlayerState()
            .onEach {
                playerState = it
                if (it is PlayerState.Prepared) playerControl.hideNotification()
            }
            .launchIn(viewModelScope)
    }

    fun removePlayerControl() {
        playerControl = null
    }

    fun onPlayerButtonClicked() {
        if (playerState is PlayerState.Default) return

        if (playerState is PlayerState.Playing) {
            playerControl?.pausePlayer()
        } else {
            playerControl?.startPlayer()
        }
    }

    fun onViewPaused() {
        if (playerState is PlayerState.Playing) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission()
            } else {
                playerControl?.showNotification()
            }
        }
    }

    fun onViewResumed() {
        playerControl?.hideNotification()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermission() {
        register.request(Manifest.permission.POST_NOTIFICATIONS)
            .onEach { result ->
                when (result) {
                    is PermissionResult.Granted -> {
                        playerControl?.showNotification()
                        permissionState.send(PermissionResultState.GRANTED)
                    }

                    is PermissionResult.Denied.NeedsRationale -> permissionState.send(PermissionResultState.NEEDS_RATIONALE)

                    is PermissionResult.Denied.DeniedPermanently -> {
                        playerControl?.pausePlayer()
                        permissionState.send(PermissionResultState.DENIED_PERMANENTLY)
                    }
                    PermissionResult.Cancelled -> return@onEach
                }
            }
            .launchIn(viewModelScope)
    }
}
