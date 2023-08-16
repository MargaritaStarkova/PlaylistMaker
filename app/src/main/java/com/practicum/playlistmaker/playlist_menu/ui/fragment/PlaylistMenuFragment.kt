package com.practicum.playlistmaker.playlist_menu.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.debounce
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentPlaylistMenuBinding
import com.practicum.playlistmaker.library.ui.models.ScreenState
import com.practicum.playlistmaker.player.ui.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import com.practicum.playlistmaker.playlist_menu.ui.bottom_sheet.BottomSheetMenu
import com.practicum.playlistmaker.playlist_menu.ui.bottom_sheet.BottomSheetSetuper
import com.practicum.playlistmaker.playlist_menu.ui.models.PlaylistMenuState
import com.practicum.playlistmaker.playlist_menu.ui.view_model.PlaylistMenuViewModel
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.fragment.TrackAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistMenuFragment : Fragment(R.layout.fragment_playlist_menu) {
    
    private val binding by viewBinding<FragmentPlaylistMenuBinding>()
    private val viewModel by viewModel<PlaylistMenuViewModel>()
    
    private var trackAdapter: PlaylistMenuAdapter? = null
    private var onClickDebounce: ((TrackModel) -> Unit)? = null
    private var onLongClickDebounce: ((TrackModel) -> Unit)? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initPlaylist()
        initObserver()
        initBottomSheetBehavior()
        initClickDebounce()
        initAdapter()
        initListeners()
        
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        trackAdapter = null
        onClickDebounce = null
        onLongClickDebounce = null
    }
    
    private fun initPlaylist() {
        val playlistId: Int = requireArguments().getInt(KEY_ID)
        
        viewModel.fillData(playlistId = playlistId)
    }
    
    private fun refreshPlaylistInfo() {
        
        val playlistDuration: Int = viewModel.getPlaylistDuration()
        val tracksCount: Int = viewModel.getTracksCount()
        
        binding.tvPlaylistDuration.text =
            resources.getQuantityString(R.plurals.minutes, playlistDuration, playlistDuration)
        
        binding.tvPlaylistTracksCount.text = resources.getQuantityString(
            R.plurals.tracks, tracksCount, tracksCount
        )
    }
    
    private fun drawPlaylist(playlistModel: PlaylistModel) {
        with(binding) {
        
            ivPlaylistCover.setImage(
                url = playlistModel.coverImageUrl,
                placeholder = R.drawable.placeholder,
            )
        
            tvPlaylistName.text = playlistModel.playlistName
            tvPlaylistDescription.text = playlistModel.playlistDescription
        
            if (playlistModel.playlistDescription.isEmpty()) {
                tvPlaylistDescription.visibility = View.GONE
            }
        }
    }
    
    private fun initBottomSheetBehavior() {
        val bottomSheetSetuper = BottomSheetSetuper(activity)
        bottomSheetSetuper.setupRatio(
            container = binding.bottomSheetTrackList, percentage = PERCENT_OCCUPIED_BY_BOTTOM_SHEET
        )
    }
    
    private fun initClickDebounce() {
        onClickDebounce = debounce(delayMillis = CLICK_DEBOUNCE_DELAY_MILLIS,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false,
            action = { track ->
                findNavController().navigate(
                    R.id.action_playlistMenuFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track)
                )
            })
    
        onLongClickDebounce = debounce(delayMillis = CLICK_DEBOUNCE_DELAY_MILLIS,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false,
            action = { track ->
                showDialog(track)
            })
    }
    
    private fun initAdapter() {
        trackAdapter = PlaylistMenuAdapter(object : TrackAdapter.TrackClickListener {
            override fun onTrackClick(track: TrackModel) {
                onClickDebounce?.let { it(track) }
            }
            
        }, object : TrackAdapter.LongClickListener {
            override fun onTrackClick(track: TrackModel) {
                onLongClickDebounce?.let { it(track) }
            }
        })
        
        binding.rvTrackList.adapter = trackAdapter
    }
    
    private fun initListeners() {
    
        with(binding) {
        
            tbNavigation.setNavigationOnClickListener {
                goBack()
            }
        
            ivShare.setOnClickListener {
                viewModel.shareClicked()
            }
        
            ivMore.setOnClickListener {
                openMenu(viewModel.getPlaylist())
            }
        }
    }
    
    private fun openMenu(playlist: PlaylistModel) {
        findNavController().navigate(
            R.id.action_playlistMenuFragment_to_bottomSheetMenu,
            BottomSheetMenu.createArgs(playlist)
        )
    }
    
    private fun goBack() {
        findNavController().navigateUp()
    }
    
    private fun initObserver() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.contentFlow.collect { screenState ->
                render(screenState)
            }
        }
    }
    
    private fun render(state: PlaylistMenuState) {
        
        when (state) {
            is PlaylistMenuState.Content -> showContent(state.content, state.bottomListState)
            PlaylistMenuState.EmptyShare -> showMessage()
            is PlaylistMenuState.Share -> {
                val messageCreator = MessageCreator(requireContext())
                viewModel.sharePlaylist(messageCreator.create(state.playlist))
            }
            
            PlaylistMenuState.DefaultState -> {}
        }
    }
    
    private fun showContent(content: PlaylistModel, bottomListState: ScreenState ) {
        drawPlaylist(content)
        refreshPlaylistInfo()
        
        when (bottomListState) {
            is ScreenState.Content<*> -> {
                with(binding) {
                    rvTrackList.visibility = View.VISIBLE
                    ivPlaceholder.visibility = View.GONE
                    tvPlaceholder.visibility = View.GONE
                }
    
                trackAdapter?.apply {
                    trackList.clear()
                    trackList.addAll(content.trackList)
                    notifyDataSetChanged()
                }
            }
            ScreenState.Empty -> showPlaceholder()
        }
    }
    
    private fun showPlaceholder() {
        with(binding) {
            rvTrackList.visibility = View.GONE
            ivPlaceholder.visibility = View.VISIBLE
            tvPlaceholder.visibility = View.VISIBLE
        }
    }
    
    private fun showMessage() {
        val message = getString(R.string.empty_track_list)
        Snackbar
            .make(requireContext(), binding.containerLayout, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.blue))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.deep_white))
            .setDuration(MESSAGE_DURATION_MILLIS)
            .show()
    }
    
    private fun showDialog(track: TrackModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track))
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                
                viewModel.deleteTrack(track)
                refreshPlaylistInfo()
            }
            .show()
    }
    
    companion object {
    
        const val KEY_ID = "key_id"
        private const val PERCENT_OCCUPIED_BY_BOTTOM_SHEET = 0.33f
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 300L
        private const val MESSAGE_DURATION_MILLIS = 2000
    
        fun createArgs(id: Int): Bundle = bundleOf(
            KEY_ID to id
        )
    }
}