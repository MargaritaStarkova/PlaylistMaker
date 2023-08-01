package com.practicum.playlistmaker.library.ui.bottom_sheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ItemViewBottomSheetBinding
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

class BottomSheetAdapter(private val clickListener: PlaylistClickListener) :
    RecyclerView.Adapter<BottomSheetViewHolder>() {
    
    val list = ArrayList<PlaylistModel>()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        return BottomSheetViewHolder(
            ItemViewBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    
    override fun getItemCount() = list.size
    
    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val playlistItem = list[holder.adapterPosition]
        holder.bind(playlistItem)
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlistItem) }
    }
    
    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: PlaylistModel)
    }
}