package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import com.practicum.playlistmaker.utils.router.NavigationRouter

class SettingsActivity : AppCompatActivity() {
    
    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    
    private val viewModel by lazy {
        ViewModelProvider(
            this, SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]
    }
    private val navigationRouter by lazy { NavigationRouter(this) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        viewModel.observeThemeSwitcherState().observe(this) { isChecked ->
            binding.themeSwitcher.isChecked = isChecked
        }
        
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitcherClicked(isChecked)
        }
    
        binding.navigationToolbar.setNavigationOnClickListener {
            navigationRouter.goBack()
        }
        
        binding.share.setOnClickListener {
            viewModel.onShareAppClicked()
        }
    
        binding.support.setOnClickListener {
            viewModel.onWriteSupportClicked()
        }
    
        binding.termsOfUse.setOnClickListener {
            viewModel.termsOfUseClicked()
        }
    }
}