package com.practicum.playlistmaker.library.ui.root_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment(R.layout.fragment_library) {
    
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var tabMediator: TabLayoutMediator
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLibraryBinding.bind(view)
        
        binding.viewPager.adapter = LibraryViewPagerAdapter(childFragmentManager, lifecycle)
        
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                else -> tab.text = getString(R.string.playlists)
            }
        }
        
        tabMediator.attach()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
        _binding = null
    }

}