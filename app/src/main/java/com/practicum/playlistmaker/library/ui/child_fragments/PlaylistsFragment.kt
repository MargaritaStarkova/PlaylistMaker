package com.practicum.playlistmaker.library.ui.child_fragments

import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.PlaylistsFragmentBinding
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(R.layout.playlists_fragment) {
    
    private val binding by viewBinding<PlaylistsFragmentBinding>()
    private val viewModel by viewModel<PlaylistsViewModel>()
    
    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
