package com.practicum.playlistmaker.player.ui.fragment

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.millisConverter
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.library.ui.bottom_sheet.BottomSheet
import com.practicum.playlistmaker.player.ui.models.PlayStatus
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {
    
    private val binding by viewBinding<FragmentAudioPlayerBinding>()
    private val viewModel by viewModel<AudioPlayerViewModel>()
    
    private lateinit var track: TrackModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        track = requireArguments()
            .getString(KEY_TRACK)
            ?.let { Json.decodeFromString<TrackModel>(it) } ?: TrackModel.emptyTrack
    
        viewModel.preparingPlayer(track.previewUrl)
        viewModel.isFavorite(track.trackId)
        
        viewModel.apply {
            observeFavoriteTrack().observe(viewLifecycleOwner) { isFavorite ->
                renderLikeButton(isFavorite)
            }
            observePlayStatus().observe(viewLifecycleOwner) { playingStatus ->
                renderPlayingContent(playingStatus)
            
            }
        }
    
        drawTrack(track)
        initListeners()
    }
    
    private fun renderLikeButton(isFavorite: Boolean) {
        val imageResource = if (isFavorite) R.drawable.button_liked
        else R.drawable.button_unliked
        
        binding.likeButton.setImageResource(imageResource)
    }
    
    private fun renderPlayingContent(status: PlayStatus) {
        
        val transitionDrawable = TransitionDrawable(
            arrayOf(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.button_play_not_prepared
                ), AppCompatResources.getDrawable(requireContext(), R.drawable.button_play)
            )
        )
        
        when (status) {
            is PlayStatus.NotConnected, is PlayStatus.Loading -> {
                showMessage(status)
            }
            
            is PlayStatus.Playing -> {
                startAnimation(binding.playButton)
                renderPlayButton(status.imageResource, status.playProgress)
            }
            
            is PlayStatus.Paused -> {
                renderPlayButton(status.imageResource, status.playProgress)
            }
            
            is PlayStatus.Ready -> {
                binding.playButton.setImageDrawable(transitionDrawable)
                transitionDrawable.startTransition(TRANSITION_DURATION)
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        viewModel.onViewPaused()
    }
    
    private fun showMessage(status: PlayStatus) {
        
        when (status) {
            is PlayStatus.NotConnected -> {
                Toast
                    .makeText(
                        requireContext(), getString(R.string.playing_error), Toast.LENGTH_SHORT
                    )
                    .show()
            }
            
            else -> Toast
                .makeText(requireContext(), getString(R.string.still_loading), Toast.LENGTH_SHORT)
                .show()
        }
    }
    
    private fun startAnimation(button: ImageButton) {
        button.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(), R.anim.scale
            )
        )
    }
    
    private fun renderPlayButton(imageResource: Int, currentPositionMediaPlayer: Int) {
        binding.playButton.setImageResource(imageResource)
        binding.excerptDuration.text = currentPositionMediaPlayer.millisConverter()
    }
    
    private fun drawTrack(trackModel: TrackModel) {
        
        val cornerRadius =
            requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_8dp)
        
        binding.apply {
            
            cover.setImage(
                context = requireContext(),
                url = trackModel.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"),
                placeholder = R.drawable.placeholder,
                cornerRadius = cornerRadius,
            )
            excerptDuration.text = AudioPlayerViewModel.START_POSITION.millisConverter()
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
                startAnimation(it as ImageButton)
                viewModel.playButtonClicked(track.previewUrl)
            }
    
            likeButton.setOnClickListener {
                startAnimation(it as ImageButton)
                viewModel.toggleFavorite(track)
            }
    
            addButton.setOnClickListener {
                startAnimation(it as ImageButton)
                findNavController().navigate(
                    R.id.action_audioPlayerFragment_to_bottomSheet, BottomSheet.createArgs(track)
                )
            }
        }
    }
    
    companion object {
        const val KEY_TRACK = "track"
        private const val TRANSITION_DURATION = 1000
    
        fun createArgs(track: TrackModel): Bundle = bundleOf(
            KEY_TRACK to Json.encodeToString(track)
        )
    }
}
