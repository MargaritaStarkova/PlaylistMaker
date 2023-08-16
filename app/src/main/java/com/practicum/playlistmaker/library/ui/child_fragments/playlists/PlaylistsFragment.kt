package com.practicum.playlistmaker.library.ui.child_fragments.playlists

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.root.HostActivity
import com.practicum.playlistmaker.core.utils.debounce
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.ui.models.ScreenState
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import com.practicum.playlistmaker.playlist_menu.ui.fragment.PlaylistMenuFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {
    
    private val binding by viewBinding<FragmentPlaylistsBinding>()
    private val viewModel by viewModel<PlaylistsViewModel>()
    
    private var playlistsAdapter: PlaylistsAdapter? = null
    private var onClickDebounce: ((PlaylistModel) -> Unit)? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        onClickDebounce = debounce(delayMillis = CLICK_DEBOUNCE_DELAY_MILLIS,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false,
            action = { playlist ->
                findNavController().navigate(
                    R.id.action_libraryFragment_to_playlistMenuFragment,
                    PlaylistMenuFragment.createArgs(playlist.id)
                )
            })
        
        initAdapter()
        
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.contentFlow.collect { screenState ->
                render(screenState)
            }
        }
        
        binding.newPlaylistBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_libraryFragment_to_newPlaylistFragment
            )
        }
    }
    
    private fun render(state: ScreenState) {
        when (state) {
            is ScreenState.Content<*> -> {
                @Suppress("UNCHECKED_CAST") showContent(state.contentList as List<PlaylistModel>)
            }
            
            ScreenState.Empty -> {
                showPlaceholder()
            }
        }
    }
    
    private fun showPlaceholder() {
        with(binding) {
            placeholdersGroup.visibility = View.VISIBLE
            rvPlaylists.visibility = View.GONE
        }
        
    }
    
    private fun showContent(content: List<PlaylistModel>) {
        with(binding) {
            placeholdersGroup.visibility = View.GONE
            rvPlaylists.visibility = View.VISIBLE
        }
    
    
        playlistsAdapter?.let {
            it.playlists.clear()
            it.playlists.addAll(content)
            it.notifyDataSetChanged()
        }
    }
    
    private fun initAdapter() {
        playlistsAdapter = PlaylistsAdapter { playlist ->
            (activity as HostActivity).animateBottomNavigationView()
            onClickDebounce?.let { it(playlist) }
        }
        
        binding.rvPlaylists.adapter = playlistsAdapter
        binding.rvPlaylists.addItemDecoration(PlaylistsOffsetItemDecoration(requireContext()))
    }
    
    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 300L
        fun newInstance() = PlaylistsFragment()
    }
}
