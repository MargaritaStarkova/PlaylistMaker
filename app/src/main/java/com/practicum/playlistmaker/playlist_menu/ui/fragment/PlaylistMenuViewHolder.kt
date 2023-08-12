package com.practicum.playlistmaker.playlist_menu.ui.fragment

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.millisConverter
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.databinding.ItemViewTrackBinding
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.fragment.TrackViewHolder

class PlaylistMenuViewHolder(
    private val binding: ItemViewTrackBinding,
) : TrackViewHolder(binding) {
    
    override fun bind(model: TrackModel) {
        val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_2dp)
        
        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName
        binding.trackTime.text = model.trackTimeMillis.millisConverter()
        
        binding.cover.setImage(
            url = model.artworkUrl100.replaceAfterLast("/", "60x60bb.jpg"),
            placeholder = R.drawable.placeholder,
            cornerRadius = cornerRadius,
        )
    }
}
