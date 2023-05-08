package com.practicum.playlistmaker.search.ui.activity

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.models.TrackModel

class TrackAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    val trackList = ArrayList<TrackModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackViewHolder(parent)

    override fun getItemCount() = trackList.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(trackList[position]) }

    }

    fun interface TrackClickListener {
        fun onTrackClick(track: TrackModel)
    }

}