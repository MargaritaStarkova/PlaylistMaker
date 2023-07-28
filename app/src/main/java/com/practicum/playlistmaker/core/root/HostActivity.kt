package com.practicum.playlistmaker.core.root

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R

class HostActivity : AppCompatActivity(R.layout.activity_host) {
    
    private lateinit var bottomNavigationView: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)
        
        
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.audioPlayerFragment, R.id.newPlaylistFragment, R.id.bottomSheet -> {
                    bottomNavigationView.visibility = View.GONE
                }
                
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }
    
    fun animateBottomNavigationView() {
        bottomNavigationView.visibility = View.GONE
    }
}
