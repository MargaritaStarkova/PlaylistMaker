package com.practicum.playlistmaker.utils.router

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerActivity

class NavigationRouter(
    private val activity: AppCompatActivity
) {

    companion object {
        private const val TRACK_MODEL = "track_model"
    }

    fun openAudioPlayer(track: TrackModel) {
        val intent = Intent(activity, AudioPlayerActivity::class.java)
        intent.putExtra(TRACK_MODEL, Gson().toJson(track))
        activity.startActivity(intent)
    }

    fun getTrackInfo(): TrackModel {
        return Gson().fromJson((activity.intent.getStringExtra(TRACK_MODEL)), TrackModel::class.java)
    }

    fun goBack() {
        activity.finish()
    }
}