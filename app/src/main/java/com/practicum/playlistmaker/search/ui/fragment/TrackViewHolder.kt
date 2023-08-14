package com.practicum.playlistmaker.search.ui.fragment

import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.millisConverter
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.databinding.ItemViewTrackBinding
import com.practicum.playlistmaker.search.domain.models.TrackModel

open class TrackViewHolder(
    private val binding: ItemViewTrackBinding,
) : RecyclerView.ViewHolder(binding.root) {
    
    open fun bind(
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
            url = model.artworkUrl100,
            placeholder = R.drawable.placeholder,
            cornerRadius = cornerRadius,
        )
        
        itemView.setOnClickListener { clickListener.onTrackClick(model) }
    }
}
