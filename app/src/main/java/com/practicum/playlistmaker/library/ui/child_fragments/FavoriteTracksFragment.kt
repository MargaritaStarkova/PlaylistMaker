package com.practicum.playlistmaker.library.ui.child_fragments

import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FavoriteTracksFragmentBinding
import com.practicum.playlistmaker.library.ui.view_model.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment(R.layout.favorite_tracks_fragment) {
    
    private val binding by viewBinding<FavoriteTracksFragmentBinding>()
    private val viewModel by viewModel<FavoriteTracksViewModel>()
    
    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}

