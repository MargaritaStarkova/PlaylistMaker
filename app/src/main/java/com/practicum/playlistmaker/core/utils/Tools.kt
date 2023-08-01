package com.practicum.playlistmaker.core.utils

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

fun ImageView.setImage(url: String, placeholder: Int, cornerRadius: Int) {
    Glide
        .with(this.context)
        .load(url)
        .placeholder(placeholder)
        .transform(CenterCrop(), RoundedCorners(cornerRadius))
        .into(this)
}

fun ImageView.setImage(uri: Uri, cornerRadius: Int) {
    Glide
        .with(this.context)
        .load(uri)
        .transform(CenterCrop(), RoundedCorners(cornerRadius))
        .into(this)
}

    fun Int.millisConverter(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
    }

