package com.practicum.playlistmaker.search.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackItemViewBinding
import com.practicum.playlistmaker.search.domain.models.TrackModel

class TrackAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    val trackList = ArrayList<TrackModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackViewHolder(
        TrackItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = trackList.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val trackItem = trackList[holder.adapterPosition]
        holder.bind(trackItem)
        holder.itemView.setOnClickListener { clickListener.onTrackClick(trackItem) }
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: TrackModel)
    }
}