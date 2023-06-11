package com.practicum.playlistmaker.core.utils.tools

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

    fun ImageView.setImage(context: Context, url: String, placeholder: Int, cornerRadius: Int,) {
        Glide
            .with(context)
            .load(url)
            .placeholder(placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(this)
    }

    fun Int.millisConverter(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
    }
