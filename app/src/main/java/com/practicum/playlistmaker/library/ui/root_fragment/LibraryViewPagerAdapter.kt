package com.practicum.playlistmaker.library.ui.root_fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.library.ui.child_fragments.favorite_tracks.FavoriteTracksFragment
import com.practicum.playlistmaker.library.ui.child_fragments.playlists.PlaylistsFragment

class LibraryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    
    override fun getItemCount() = 2
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteTracksFragment()
            else -> PlaylistsFragment.newInstance()
        }
    }
}