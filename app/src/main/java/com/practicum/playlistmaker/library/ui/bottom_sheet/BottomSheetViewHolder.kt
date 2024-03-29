package com.practicum.playlistmaker.library.ui.bottom_sheet

import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.databinding.ItemViewBottomSheetBinding
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

class BottomSheetViewHolder(
    private val binding: ItemViewBottomSheetBinding,
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(model: PlaylistModel) {
        val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_2dp)
        
        binding.tvPlaylistName.text = model.playlistName
        binding.tvTracksCount.text = itemView.resources.getQuantityString(R.plurals.tracks, model.tracksCount, model.tracksCount)
        
        binding.ivCover.setImage(
            url = model.coverImageUrl,
            placeholder = R.drawable.placeholder,
            cornerRadius = cornerRadius,
        )
    }
}
