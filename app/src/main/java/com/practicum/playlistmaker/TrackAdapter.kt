package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val trackData: List<TrackData>) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackViewHolder(parent)

    override fun getItemCount() = trackData.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackData[position])
    }

}