package com.practicum.playlistmaker.library.ui.child_fragments.playlists

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.ui.models.PlaylistsScreenState
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {
    
    private val binding by viewBinding<FragmentPlaylistsBinding>()
    private val viewModel by viewModel<PlaylistsViewModel>()
    
    private var job: Job? = null
    
    private lateinit var playlistsAdapter: PlaylistsAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initAdapter()
        
       job = lifecycleScope.launch {
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
    
    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }
    
    private fun render(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.Content -> showContent(state.content)
            PlaylistsScreenState.Empty -> showPlaceholder()
        }
    }
    
    private fun showPlaceholder() {
        binding.apply {
            placeholdersGroup.visibility = View.VISIBLE
            playlists.visibility = View.GONE
        }
        
    }
    
    private fun showContent(content: List<PlaylistModel>) {
        
        binding.apply {
            placeholdersGroup.visibility = View.GONE
            playlists.visibility = View.VISIBLE
        }
        
        
        playlistsAdapter.apply {
            playlists.clear()
            playlists.addAll(content)
            notifyDataSetChanged()
        }
    }
    
    private fun initAdapter() {
        playlistsAdapter = PlaylistsAdapter { playlist ->
            
            Toast
                .makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT)
                .show()
            
        }
        
        binding.playlists.adapter = playlistsAdapter
        binding.playlists.addItemDecoration(PlaylistsOffsetItemDecoration(requireContext()))
    }
    
    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
