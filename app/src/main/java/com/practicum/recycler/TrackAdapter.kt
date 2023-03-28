package com.practicum.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.api.TrackData

class TrackAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var trackDataList = ArrayList<TrackData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackViewHolder(parent)

    override fun getItemCount() = trackDataList.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackDataList[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(trackDataList[position]) }

    }

    fun interface TrackClickListener {
        fun onTrackClick(track: TrackData)
    }

}