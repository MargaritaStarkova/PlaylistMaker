package com.practicum.playlistmaker.utils.router

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class ToolsRouter {

    fun glideProvider(context: Context, url: String, placeholder: Int, cornerRadius: Int, view: ImageView) {
        Glide
            .with(context)
            .load(url)
            .placeholder(placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(view)
    }

    fun millisConverter(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }
}