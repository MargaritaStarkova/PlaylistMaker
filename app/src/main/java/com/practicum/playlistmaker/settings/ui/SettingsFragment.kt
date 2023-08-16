package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    

    private val binding by viewBinding<FragmentSettingsBinding>()
    private val viewModel by viewModel<SettingsViewModel>()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel
            .themeSwitcherState
            .observe(viewLifecycleOwner) { isChecked ->
                binding.themeSwitcher.isChecked = isChecked
            }
        
        binding.apply {
            themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onThemeSwitcherClicked(isChecked)
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