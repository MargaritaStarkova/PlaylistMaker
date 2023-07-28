package com.practicum.playlistmaker.search.ui.fragment

import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.core.utils.millisConverter
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.databinding.ItemViewTrackBinding

class TrackViewHolder(
    private val binding: ItemViewTrackBinding
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(model: TrackModel) {
        val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_2dp)
    
        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName
        binding.trackTime.text = model.trackTimeMillis.millisConverter()
    
        binding.cover.setImage(
            context = itemView.context,
            url = model.artworkUrl100,
            placeholder = R.drawable.placeholder,
            cornerRadius = cornerRadius,
        )
    }
}
