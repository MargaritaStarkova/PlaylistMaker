package com.practicum.playlistmaker.player.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.application.App
import com.practicum.playlistmaker.player.domain.api.PlayerControl
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.fragment.AudioPlayerFragment.Companion.KEY_DESCRIPTION
import com.practicum.playlistmaker.player.ui.fragment.AudioPlayerFragment.Companion.KEY_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PlayerService : Service(), PlayerControl {

    private val binder = AudioServiceBinder()
    private val player: MediaPlayer by inject()
    private var timerJob: Job? = null
    private var songUrl = EMPTY
    private var description = EMPTY

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default())

    private var playerState: PlayerState
        get() = _playerState.value
        set(value) {
            _playerState.value = value
        }

    override fun observePlayerState() = _playerState.asStateFlow()

    override fun onBind(intent: Intent?): IBinder {
        songUrl = intent?.getStringExtra(KEY_URL) ?: EMPTY
        description = intent?.getStringExtra(KEY_DESCRIPTION) ?: EMPTY
        initMediaPlayer()

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun startPlayer() {
        player.start()
        startTimer()
    }

    override fun pausePlayer() {
        player.pause()
        timerJob?.cancel()
        playerState = PlayerState.Paused(player.currentPosition)
    }

    override fun showNotification() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(
            this, ServiceCompat.STOP_FOREGROUND_REMOVE
        )
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (player.isPlaying) {
                delay(DELAY_MILLIS)
                playerState = PlayerState.Playing(player.currentPosition)
            }
        }
    }

    private fun initMediaPlayer() {
        if (songUrl.isEmpty()) return

        player.setDataSource(songUrl)
        player.prepareAsync()
        player.setOnPreparedListener {
            playerState = PlayerState.Prepared()
        }
        player.setOnCompletionListener {
            timerJob?.cancel()
            playerState = PlayerState.Prepared()
        }
    }

    private fun releasePlayer() {
        player.stop()
        timerJob?.cancel()
        playerState = PlayerState.Default()
        player.setOnPreparedListener(null)
        player.setOnCompletionListener(null)
        player.release()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name)).setContentText(description)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE).build()
    }

    inner class AudioServiceBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    private companion object {
        const val DELAY_MILLIS = 300L
        const val SERVICE_NOTIFICATION_ID = 100
        const val EMPTY = ""
    }
}
