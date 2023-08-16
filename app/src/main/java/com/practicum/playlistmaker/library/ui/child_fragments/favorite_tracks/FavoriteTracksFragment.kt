package com.practicum.playlistmaker.library.ui.child_fragments.favorite_tracks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.root.HostActivity
import com.practicum.playlistmaker.core.utils.debounce
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.library.ui.models.ScreenState
import com.practicum.playlistmaker.library.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.fragment.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment(R.layout.fragment_favorite_tracks) {
    
    private val binding by viewBinding<FragmentFavoriteTracksBinding>()
    private val viewModel by viewModel<FavoriteTracksViewModel>()
    
    private var trackAdapter: TrackAdapter? = null
    private var onClickDebounce: ((TrackModel) -> Unit)? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        onClickDebounce = debounce(delayMillis = CLICK_DEBOUNCE_DELAY_MILLIS,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false,
            action = { track ->
                findNavController().navigate(
                    R.id.action_libraryFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track)
                )
            })
    
        viewModel.contentState.observe(viewLifecycleOwner) { contentState ->
            render(contentState)
        }
    
        initAdapter()
    }
    
    private fun initAdapter() {
        trackAdapter = TrackAdapter(
            clickListener = (TrackAdapter.TrackClickListener { track ->
                (activity as HostActivity).animateBottomNavigationView()
                onClickDebounce?.let { it(track) }
            }),
        )
        binding.rvTrackList.adapter = trackAdapter
    }
    
    private fun render(state: ScreenState) {
        when (state) {
            is ScreenState.Content<*> -> {
                @Suppress("UNCHECKED_CAST") showContent(state.contentList as List<TrackModel>)
            }
            
            ScreenState.Empty -> showMessage()
        }
    }
    
    private fun showMessage() {
        with(binding) {
            placeholder.visibility = View.VISIBLE
            rvTrackList.visibility = View.GONE
        }
    }
    
    private fun showContent(list: List<TrackModel>) {
        with(binding) {
            placeholder.visibility = View.GONE
            rvTrackList.visibility = View.VISIBLE
        }
        trackAdapter?.let {
            it.trackList.clear()
            it.trackList.addAll(list)
            it.notifyDataSetChanged()
        }
    }
    
    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 300L
    }
}

