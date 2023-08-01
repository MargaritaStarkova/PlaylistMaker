package com.practicum.playlistmaker.library.ui.bottom_sheet

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.R.id.design_bottom_sheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.BottomSheetBinding
import com.practicum.playlistmaker.library.ui.models.BottomSheetState
import com.practicum.playlistmaker.library.ui.view_model.BottomSheetViewModel
import com.practicum.playlistmaker.player.ui.fragment.AudioPlayerFragment.Companion.KEY_TRACK
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomSheet : BottomSheetDialogFragment(R.layout.bottom_sheet) {
    
    private val binding by viewBinding<BottomSheetBinding>()
    private val viewModel by viewModel<BottomSheetViewModel>()
    
    private lateinit var playlistsAdapter: BottomSheetAdapter
    private lateinit var track: TrackModel
    
    override fun onStart() {
        super.onStart()
         setupRatio(requireContext(), dialog as BottomSheetDialog, 100)
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
            
            viewModel.onPlaylistClicked(playlist, track)
            
        }
        binding.playlistsRecycler.adapter = playlistsAdapter
    }
    
    private fun initListeners() {
        binding.createPlaylistBtn.setOnClickListener {
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
        binding.playlistsRecycler.visibility = View.VISIBLE
        playlistsAdapter.apply {
            list.clear()
            list.addAll(content)
            notifyDataSetChanged()
        }
    }
    
    private fun setupRatio(context: Context, bottomSheetDialog: BottomSheetDialog, percetage: Int) {
        
        val bottomSheet = bottomSheetDialog.findViewById<View>(design_bottom_sheet) as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight(context, percetage)
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    
    }
    
    private fun getBottomSheetDialogDefaultHeight(context: Context, percetage: Int): Int {
        return getWindowHeight(context) * percetage / 100
    }
    
    private fun getWindowHeight(context: Context): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        
        @Suppress("DEPRECATION") requireActivity().windowManager.defaultDisplay.getMetrics(
            displayMetrics
        )
        
        return displayMetrics.heightPixels
    }
    
    companion object {
        
        fun createArgs(track: TrackModel): Bundle = bundleOf(
            KEY_TRACK to Json.encodeToString(track)
        )
        
        private const val MESSAGE_DURATION = 2000
    }
}