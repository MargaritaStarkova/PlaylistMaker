package com.practicum.playlistmaker.utils.router

import android.os.Handler
import android.os.Looper

class HandlerRouter {

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, DELAY)
        }
        return current
    }

    fun searchDebounce(r: Runnable) {
        stopRunnable()
        handler.postDelayed(r, SEARCH_DEBOUNCE_DELAY)
    }

    fun startPlaying(r: Runnable) {
        handler.postDelayed(r, DELAY)
    }

    fun stopRunnable() {
        handler.removeCallbacksAndMessages(null)
    }
    
    companion object {
        private const val DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}