package com.practicum.playlistmaker.library.ui.child_fragments.playlists

import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.countTracksStringFormat
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.databinding.ItemViewPlaylistBinding
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel

class PlaylistsViewHolder(
    private val binding: ItemViewPlaylistBinding,
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(model: PlaylistModel) {
        val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_8dp)
        
        binding.playlistName.text = model.playlistName
        binding.tracksCount.text = model.tracksCount.countTracksStringFormat()
        
        binding.playlistCover.setImage(
            context = itemView.context,
            url = model.coverImageUrl,
            placeholder = R.drawable.placeholder,
            cornerRadius = cornerRadius,
        )
    }
}
