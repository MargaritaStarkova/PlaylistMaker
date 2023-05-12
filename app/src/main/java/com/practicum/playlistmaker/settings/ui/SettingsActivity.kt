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
        
        binding.apply {
            themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onThemeSwitcherClicked(isChecked)
            }
    
            navigationToolbar.setNavigationOnClickListener {
                navigationRouter.goBack()
            }
    
            share.setOnClickListener {
                viewModel.onShareAppClicked()
            }
    
            support.setOnClickListener {
                viewModel.onWriteSupportClicked()
            }
    
            termsOfUse.setOnClickListener {
                viewModel.termsOfUseClicked()
            }
        }
    }
}