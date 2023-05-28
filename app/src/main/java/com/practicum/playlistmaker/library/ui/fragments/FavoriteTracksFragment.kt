package com.practicum.playlistmaker.library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FavoriteTracksFragmentBinding
import com.practicum.playlistmaker.library.ui.view_model.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {
    
    private val viewModel by viewModel<FavoriteTracksViewModel>()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FavoriteTracksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}

