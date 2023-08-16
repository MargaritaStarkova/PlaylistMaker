package com.practicum.playlistmaker.playlist_redactor.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import com.practicum.playlistmaker.playlist_creator.ui.fragment.PlaylistCreatorFragment
import com.practicum.playlistmaker.playlist_menu.ui.bottom_sheet.BottomSheetMenu
import com.practicum.playlistmaker.playlist_redactor.ui.view_model.PlaylistRedactorViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistRedactorFragment : PlaylistCreatorFragment() {
    
    override val viewModel by viewModel<PlaylistRedactorViewModel>()
    
    private val binding by viewBinding<FragmentPlaylistCreatorBinding>()
    private var playlist: PlaylistModel? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        playlist = requireArguments()
            .getString(BottomSheetMenu.PLAYLIST_KEY)
            ?.let { Json.decodeFromString<PlaylistModel>(it) } ?: PlaylistModel.emptyPlaylist
        
        playlist?.let {
            drawPlaylist(it)
            viewModel.initPlaylist(it)
        }
    }
    
    override fun showAndroidXSnackbar(playlistName: String) {
        val message =
            getString(R.string.playlist) + " \"" + playlistName + "\" " + getString(R.string.changed)
        Snackbar
            .make(requireContext(), binding.containerLayout, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.blue))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.deep_white))
            .setDuration(MESSAGE_DURATION_MILLIS)
            .show()
    }
    
    override fun initBackPressed() {
        binding.tbNavigation.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun drawPlaylist(playlist: PlaylistModel) {
        
        val cornerRadius =
            requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_8dp)
        
        with(binding) {
            toolbarTitle.text = getString(R.string.edit_title)
            ivPlaylistCover.setImage(
                url = playlist.coverImageUrl,
                placeholder = R.drawable.placeholder,
                cornerRadius = cornerRadius
            )
            
            etPlaylistName.setText(playlist.playlistName)
            etPlaylistDescription.setText(playlist.playlistDescription)
            
            buttonCreate.text = getString(R.string.save_playlist)
        }
        
    }
    
    companion object {
        
        private const val PLAYLIST_KEY = "playlist_key"
        private const val MESSAGE_DURATION_MILLIS = 2000
        
        fun createArgs(playlist: PlaylistModel): Bundle = bundleOf(
            PLAYLIST_KEY to Json.encodeToString(playlist)
        )
    }
}