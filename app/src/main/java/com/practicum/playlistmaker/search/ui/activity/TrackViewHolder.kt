package com.practicum.playlistmaker.search.ui.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.utils.router.ToolsRouter

class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context).inflate(R.layout.track_list_view, parentView, false)
) {

    private val cover = itemView.findViewById<ImageView>(R.id.cover)
    private val trackName = itemView.findViewById<TextView>(R.id.track_name)
    private val artistName = itemView.findViewById<TextView>(R.id.artist_name)
    private val trackTime = itemView.findViewById<TextView>(R.id.track_time)

    private val router = ToolsRouter()

    fun bind(model: TrackModel) {
        val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_2dp)

        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = router.millisConverter(model.trackTimeMillis)

        router.glideProvider(
            context = itemView.context,
            url = model.artworkUrl100,
            placeholder = R.drawable.placeholder,
            cornerRadius = cornerRadius,
            view = cover)
    }
}
