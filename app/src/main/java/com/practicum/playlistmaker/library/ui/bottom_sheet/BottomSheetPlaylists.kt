package com.practicum.playlistmaker.library.ui.bottom_sheet

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.BottomSheetPlaylistsBinding
import com.practicum.playlistmaker.library.ui.models.BottomSheetState
import com.practicum.playlistmaker.library.ui.view_model.BottomSheetViewModel
import com.practicum.playlistmaker.player.ui.fragment.AudioPlayerFragment.Companion.KEY_TRACK
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import com.practicum.playlistmaker.playlist_menu.ui.bottom_sheet.BottomSheetSetuper
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomSheetPlaylists : BottomSheetDialogFragment(R.layout.bottom_sheet_playlists) {
    
    private val binding by viewBinding<BottomSheetPlaylistsBinding>()
    private val viewModel by viewModel<BottomSheetViewModel>()
    
    private var playlistsAdapter: BottomSheetAdapter? = null
    private var track: TrackModel? = null
    
    override fun onStart() {
        super.onStart()
        val bottomSheetSetuper = BottomSheetSetuper(activity)
        bottomSheetSetuper.setupRatio(
            dialog as BottomSheetDialog, PERCENT_OCCUPIED_BY_BOTTOM_SHEET
        )
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        track = requireArguments()
            .getString(KEY_TRACK)
            ?.let { Json.decodeFromString<TrackModel>(it) } ?: TrackModel.emptyTrack
    
        initAdapter()
        initListeners()
        initObserver()
    
    }

    private fun initAdapter() {
        playlistsAdapter = BottomSheetAdapter { playlist ->
            track?.let { viewModel.onPlaylistClicked(playlist, it) }
        }
        
        binding.rvPlaylists.adapter = playlistsAdapter
    }
    
    private fun initListeners() {
        binding.bCreatePlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_bottomSheet_to_newPlaylistFragment
            )
        }
    }
    
    private fun initObserver() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.contentFlow.collect { screenState ->
                render(screenState)
            }
        }
    }
    
    private fun render(state: BottomSheetState) {
        when (state) {
            is BottomSheetState.AddedAlready -> {
                val message =
                    getString(R.string.already_added) + " \"" + state.playlistModel.playlistName + "\" "
                Toast
                    .makeText(requireContext(), message, Toast.LENGTH_SHORT)
                    .show()
            }
            
            is BottomSheetState.AddedNow -> {
                val message =
                    getString(R.string.added) + " \"" + state.playlistModel.playlistName + "\" "
                
                showMessage(message)
                dialog?.cancel()
            }
            
            else -> showContent(state.content)
        }
    }
    
    private fun showMessage(message: String) {
        Snackbar
            .make(
                requireContext(),
                requireActivity().findViewById(R.id.container_layout),
                message,
                Snackbar.LENGTH_SHORT
            )
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.blue))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.deep_white))
            .setDuration(MESSAGE_DURATION)
            .show()
    }
    
    private fun showContent(content: List<PlaylistModel>) {
        binding.rvPlaylists.visibility = View.VISIBLE
        playlistsAdapter?.let {
            it.list.clear()
            it.list.addAll(content)
            it.notifyDataSetChanged()
        }
    }
    
    companion object {
    
        fun createArgs(track: TrackModel): Bundle = bundleOf(
            KEY_TRACK to Json.encodeToString(track)
        )
    
        private const val PERCENT_OCCUPIED_BY_BOTTOM_SHEET = 0.45f
        private const val MESSAGE_DURATION = 2000
    }
}