package com.practicum.playlistmaker.playlist_menu.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.ItemViewTrackBinding
import com.practicum.playlistmaker.search.ui.fragment.TrackAdapter
import com.practicum.playlistmaker.search.ui.fragment.TrackViewHolder

class PlaylistMenuAdapter(
    private val clickListener: TrackClickListener,
    private val longClickListener: LongClickListener,
) : TrackAdapter(clickListener, longClickListener) {
    
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList, clickListener, longClickListener)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistMenuViewHolder {
        return PlaylistMenuViewHolder(
            ItemViewTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}

