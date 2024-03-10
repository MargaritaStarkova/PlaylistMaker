package com.practicum.playlistmaker.core.root

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import com.practicum.playlistmaker.search.data.network.InternetConnectionValidator

class InternetConnectionBroadcastReceiver(
    private val validator: InternetConnectionValidator
) : BroadcastReceiver() {
    @Suppress("DEPRECATION")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != ConnectivityManager.CONNECTIVITY_ACTION) return

        if (!validator.isConnected()) {
            Toast.makeText(context, "Нет интернета", Toast.LENGTH_SHORT).show()
        }
    }
}