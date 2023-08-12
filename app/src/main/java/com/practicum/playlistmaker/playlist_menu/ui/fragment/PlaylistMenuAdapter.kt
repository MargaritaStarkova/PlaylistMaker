package com.practicum.playlistmaker.playlist_menu.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.ItemViewTrackBinding
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.fragment.TrackAdapter
import com.practicum.playlistmaker.search.ui.fragment.TrackViewHolder

class PlaylistMenuAdapter(
    private val clickListener: TrackClickListener,
    private val longClickListener: LongClickListener,
) : TrackAdapter(clickListener) {
    
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val trackItem = trackList[holder.adapterPosition]
        holder as PlaylistMenuViewHolder
        holder.bind(trackItem)
        holder.itemView.setOnClickListener { clickListener.onTrackClick(trackItem) }
        holder.itemView.setOnLongClickListener {
            longClickListener.onTrackClick(trackItem)
            return@setOnLongClickListener true
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistMenuViewHolder {
        return PlaylistMenuViewHolder(
            ItemViewTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    
    fun interface LongClickListener {
        fun onTrackClick(track: TrackModel)
    }
}

