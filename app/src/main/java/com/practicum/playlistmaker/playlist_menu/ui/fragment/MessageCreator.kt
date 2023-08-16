package com.practicum.playlistmaker.playlist_menu.ui.fragment

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.millisConverter
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

class MessageCreator(private val context: Context) {
    
    fun create(playlist: PlaylistModel): String {
        val message = StringBuilder()
        message.append("${context.getString(R.string.playlist)}: ")
        message.append("'${playlist.playlistName}'\n")
        if (playlist.playlistDescription.isNotEmpty()) {
            message.append("${playlist.playlistDescription}\n")
        }
        message.append(
            "${
                context.resources.getQuantityString(
                    R.plurals.tracks, playlist.tracksCount, playlist.tracksCount
                )
            }\n\n"
        )
        
        playlist.trackList.forEachIndexed { index, track ->
            message.append("${index + 1}. " + "${track.artistName} - ${track.trackName} (${track.trackTimeMillis.millisConverter()})\n")
        }
        return message.toString()
    }
}