package com.practicum.playlistmaker.library.ui.child_fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.root.HostActivity
import com.practicum.playlistmaker.core.utils.debounce
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FavoriteTracksFragmentBinding
import com.practicum.playlistmaker.library.ui.models.FavoriteState
import com.practicum.playlistmaker.library.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.fragment.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment(R.layout.favorite_tracks_fragment) {
    
    private lateinit var onClickDebounce: (TrackModel) -> Unit
    
    private val binding by viewBinding<FavoriteTracksFragmentBinding>()
    private val viewModel by viewModel<FavoriteTracksViewModel>()
    
    private var trackAdapter: TrackAdapter? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        onClickDebounce = debounce(delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false,
            action = { track ->
                findNavController().navigate(
                    R.id.action_libraryFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track)
                )
            })
        
        viewModel
            .observeContentState()
            .observe(viewLifecycleOwner) { contentState ->
                render(contentState)
            }
        
        initAdapter()
    }
    
    private fun initAdapter() {
        trackAdapter = TrackAdapter { track ->
            (activity as HostActivity).animateBottomNavigationView()
            onClickDebounce(track)
        }
        binding.tracksList.adapter = trackAdapter
    }
    
    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.SelectedTracks -> {
                showContent(state.trackList)
            }
            
            FavoriteState.Empty -> showMessage()
        }
    }
    
    private fun showMessage() {
        binding.apply {
            placeholder.visibility = View.VISIBLE
            tracksList.visibility = View.GONE
        }
    }
    
    private fun showContent(list: List<TrackModel>) {
        binding.apply {
            placeholder.visibility = View.GONE
            tracksList.visibility = View.VISIBLE
        }
        trackAdapter?.apply {
            trackList.clear()
            trackList.addAll(list)
            notifyDataSetChanged()
        }
    }
    
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}

