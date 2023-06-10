package com.practicum.playlistmaker.library.ui.child_fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistsFragmentBinding
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(R.layout.playlists_fragment) {
    
    private var _binding: PlaylistsFragmentBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel by viewModel<PlaylistsViewModel>()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = PlaylistsFragmentBinding.bind(view)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
