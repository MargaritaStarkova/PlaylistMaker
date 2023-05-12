package com.practicum.playlistmaker.main.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.library.ui.activity.LibraryActivity
import com.practicum.playlistmaker.main.ui.models.NavigationState
import com.practicum.playlistmaker.main.ui.view_model.MainViewModel
import com.practicum.playlistmaker.search.ui.activity.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        ViewModelProvider(
            this, MainViewModel.getViewModelFactory()
        )[MainViewModel::class.java]
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        viewModel.observeContentStateLiveData().observe(this) { navigationState ->
            navigation(navigationState)
        }
        
        initListeners()
    }
    
    private fun initListeners() {
        
        binding.searchButton.setOnClickListener {
            viewModel.onSearchButtonClicked()
        }
        binding.libraryButton.setOnClickListener {
            viewModel.onLibraryButtonClicked()
        }
        binding.settingsButton.setOnClickListener {
            viewModel.onSettingsButtonClicked()
        }
    }
    
    private fun navigation(state: NavigationState) {
        when (state) {
            NavigationState.Search -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }
            NavigationState.Library -> {
                startActivity(Intent(this, LibraryActivity::class.java))
            }
            NavigationState.Settings -> {
             startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }
}
