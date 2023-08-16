package com.practicum.playlistmaker.playlist_menu.ui.fragment

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.millisConverter
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.databinding.ItemViewTrackBinding
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.fragment.TrackAdapter
import com.practicum.playlistmaker.search.ui.fragment.TrackViewHolder

class PlaylistMenuViewHolder(
    private val binding: ItemViewTrackBinding,
) : TrackViewHolder(binding) {
    
    override fun bind(
        itemList: List<TrackModel>,
        clickListener: TrackAdapter.TrackClickListener,
        longClickListener: TrackAdapter.LongClickListener?,
    ) {
        
        val model: TrackModel = itemList[adapterPosition]
        
        val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_2dp)
        
        binding.tvTrackName.text = model.trackName
        binding.tvArtistName.text = model.artistName
        binding.tvTrackTime.text = model.trackTimeMillis.millisConverter()
        
        binding.ivCover.setImage(
            url = model.artworkUrl60,
            placeholder = R.drawable.placeholder,
            cornerRadius = cornerRadius,
        )
        
        itemView.setOnClickListener { clickListener.onTrackClick(model) }
        
        itemView.setOnLongClickListener {
            longClickListener?.onTrackClick(model)
            return@setOnLongClickListener true
        }
    }
}
