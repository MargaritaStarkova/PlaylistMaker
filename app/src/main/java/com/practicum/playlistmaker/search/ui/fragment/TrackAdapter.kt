package com.practicum.playlistmaker.search.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ItemViewTrackBinding
import com.practicum.playlistmaker.search.domain.models.TrackModel

open class TrackAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {
    
    val trackList = ArrayList<TrackModel>()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            ItemViewTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    
    override fun getItemCount(): Int {
        return trackList.size
    }
    
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val trackItem = trackList[holder.adapterPosition]
        holder.bind(trackItem)
        holder.itemView.setOnClickListener { clickListener.onTrackClick(trackItem) }
    }
    
    fun interface TrackClickListener {
        fun onTrackClick(track: TrackModel)
    }
}