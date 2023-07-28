package com.practicum.playlistmaker.core.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

    fun ImageView.setImage(context: Context, url: String, placeholder: Int, cornerRadius: Int,) {
        Glide
            .with(context)
            .load(url)
            .placeholder(placeholder)
            .transform(CenterCrop(),RoundedCorners(cornerRadius))
            .into(this)
    }

    fun ImageView.setImage(context: Context, uri: Uri, cornerRadius: Int,) {
        Glide
            .with(context)
            .load(uri)
            .transform(CenterCrop(),RoundedCorners(cornerRadius))
            .into(this)
    }

    fun Int.millisConverter(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
    }

    fun Int.countTracksStringFormat(): String {
        return when {
            this == 1 || this % 10 == 9 -> "$this трек"
            this % 10 in 6..8 -> "$this трека"
            else -> "$this треков"
        }
    }
