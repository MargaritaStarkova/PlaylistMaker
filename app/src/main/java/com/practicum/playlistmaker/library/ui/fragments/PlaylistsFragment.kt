package com.practicum.playlistmaker.library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.PlaylistsFragmentBinding
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    
    private val viewModel by viewModel<PlaylistsViewModel>()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = PlaylistsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
