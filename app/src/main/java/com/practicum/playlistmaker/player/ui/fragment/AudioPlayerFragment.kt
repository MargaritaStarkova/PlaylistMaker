package com.practicum.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.millisConverter
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.player.ui.models.PlayStatus
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.TrackModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {
    
    private val binding by viewBinding<FragmentAudioPlayerBinding>()
    private val viewModel by viewModel<AudioPlayerViewModel>()
    
    private lateinit var trackModel: TrackModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackModel = viewModel.getTrack()
        
        viewModel.apply {
            observePlayStatus().observe(viewLifecycleOwner) { playingStatus ->
                when (playingStatus) {
                    is PlayStatus.NotConnected -> {
                        showMessage()
                    }
        
                    is PlayStatus.Playing -> {
                        startAnimation()
                        renderPlayer(playingStatus.imageResource, playingStatus.playProgress)
                    }
        
                    is PlayStatus.Paused -> {
                        renderPlayer(playingStatus.imageResource, playingStatus.playProgress)
                    }
                }
            }
        }
        drawTrack(trackModel, AudioPlayerViewModel.START_POSITION)
        initListeners()
    }
    
    override fun onPause() {
        super.onPause()
        viewModel.onViewPaused()
    }
    
    private fun showMessage() {
        Toast
            .makeText(context, R.string.playing_error, Toast.LENGTH_SHORT)
            .show()
    }
    
    private fun startAnimation() {
        binding.playButton.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(), R.anim.scale
            )
        )
    }
    
    private fun renderPlayer(imageResource: Int, currentPositionMediaPlayer: Int) {
        binding.playButton.setImageResource(imageResource)
        binding.excerptDuration.text = currentPositionMediaPlayer.millisConverter()
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
                startAnimation()
                viewModel.playButtonClicked(trackModel.previewUrl)
            }
        }
    }
}
