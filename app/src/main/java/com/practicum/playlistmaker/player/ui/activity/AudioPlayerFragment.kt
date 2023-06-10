package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.tools.millisConverter
import com.practicum.playlistmaker.core.utils.tools.setImage
import com.practicum.playlistmaker.databinding.AudioPlayerActivityBinding
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.TrackModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment(R.layout.audio_player_activity) {
    
    private var _binding: AudioPlayerActivityBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AudioPlayerViewModel by viewModel()
    
    private lateinit var trackModel: TrackModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AudioPlayerActivityBinding.bind(view)
        trackModel = viewModel.getTrack()
        
        viewModel.apply {
            observePlayStatus().observe(viewLifecycleOwner) { playingStatus ->
                updatePlayButton(playingStatus.imageResource)
            }
            observePlayProgress().observe(viewLifecycleOwner) { currentPosition ->
                updateTrackDuration(currentPosition)
            }
        }
        
        drawTrack(trackModel, AudioPlayerViewModel.START_POSITION)
        initListeners()
    }
    
    override fun onPause() {
        super.onPause()
        viewModel.onViewPaused()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun drawTrack(trackModel: TrackModel, startPosition: Int) {
        
        val cornerRadius = this.resources.getDimensionPixelSize(R.dimen.corner_radius_8dp)
        
        binding.apply {
            
            cover.setImage(
                context = requireContext(),
                url = trackModel.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"),
                placeholder = R.drawable.placeholder,
                cornerRadius = cornerRadius,
            )
            excerptDuration.text = startPosition.millisConverter()
            trackName.text = trackModel.trackName
            artistName.text = trackModel.artistName
            changeableDuration.text = trackModel.trackTimeMillis.millisConverter()
            changeableAlbum.text = trackModel.collectionName
            changeableYear.text = trackModel.releaseDate.substring(0, 4)
            changeableGenre.text = trackModel.primaryGenreName
                changeableCountry.text = trackModel.country
        }
    }
    
    private fun initListeners() {
        
        binding.apply {
            navigationToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            
            playButton.setOnClickListener {
                binding.playButton.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(), R.anim.scale
                    )
                )
                viewModel.playButtonClicked(trackModel.previewUrl)
            }
        }
    }
    
    private fun updatePlayButton(imageResource: Int) {
        binding.playButton.setImageResource(imageResource)
    }
    
    private fun updateTrackDuration(currentPositionMediaPlayer: Int) {
        binding.excerptDuration.text = currentPositionMediaPlayer.millisConverter()
    }
}
