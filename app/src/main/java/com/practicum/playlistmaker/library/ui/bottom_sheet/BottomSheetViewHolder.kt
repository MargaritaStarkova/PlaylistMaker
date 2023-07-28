package com.practicum.playlistmaker.library.ui.bottom_sheet

import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.countTracksStringFormat
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.databinding.ItemViewBottomSheetBinding
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

class BottomSheetViewHolder(
    private val binding: ItemViewBottomSheetBinding,
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(model: PlaylistModel) {
        val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_2dp)
        
        binding.playlistName.text = model.playlistName
        binding.trakcsCount.text = model.tracksCount.countTracksStringFormat()
        
        binding.cover.setImage(
            context = itemView.context,
            url = model.coverImageUrl,
            placeholder = R.drawable.placeholder,
            cornerRadius = cornerRadius,
        )
    }
}
