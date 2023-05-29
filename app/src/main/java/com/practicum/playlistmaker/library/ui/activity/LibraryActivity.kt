package com.practicum.playlistmaker.library.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityLibraryBinding
import com.practicum.playlistmaker.utils.router.NavigationRouter

class LibraryActivity : AppCompatActivity() {
    
    private lateinit var tabMediator: TabLayoutMediator
    private val navigationRouter by lazy { NavigationRouter(this) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.viewPager.adapter = LibraryViewPagerAdapter(supportFragmentManager, lifecycle)
        
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                else -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()
        
        binding.navigationToolbar.setNavigationOnClickListener {
            navigationRouter.goBack()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}