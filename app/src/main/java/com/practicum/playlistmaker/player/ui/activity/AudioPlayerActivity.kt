package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.AudioPlayerActivityBinding
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.utils.router.NavigationRouter
import com.practicum.playlistmaker.utils.tools.millisConverter
import com.practicum.playlistmaker.utils.tools.setImage

class AudioPlayerActivity : AppCompatActivity() {
    
    private val viewModel by lazy {
        ViewModelProvider(
            this, AudioPlayerViewModel.getViewModelFactory(trackModel.previewUrl)
        )[AudioPlayerViewModel::class.java]
    }
    private val binding by lazy { AudioPlayerActivityBinding.inflate(layoutInflater) }
    private val navigationRouter by lazy { NavigationRouter(this) }
    private val trackModel by lazy { navigationRouter.getTrackInfo() }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        viewModel.observePlayStatus().observe(this) { playingStatus ->
            updatePlayButton(playingStatus.imageResource)
        }
        viewModel.observePlayProgress().observe(this) { currentPosition ->
            updateTrackDuration(currentPosition)
        }
        
        drawTrack(trackModel, AudioPlayerViewModel.START_POSITION)
        initListeners()
    }
    
    override fun onPause() {
        super.onPause()
        viewModel.onViewPaused()
    }
    
    private fun drawTrack(trackModel: TrackModel, startPosition: Int) {
        
        val cornerRadius = this.resources.getDimensionPixelSize(R.dimen.corner_radius_8dp)
        
        binding.cover.setImage(
            context = this,
            url = trackModel.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"),
            placeholder = R.drawable.placeholder,
            cornerRadius = cornerRadius,
        )
        binding.excerptDuration.text = startPosition.millisConverter()
        binding.trackName.text = trackModel.trackName
        binding.artistName.text = trackModel.artistName
        binding.changeableDuration.text = trackModel.trackTimeMillis.millisConverter()
        binding.changeableAlbum.text = trackModel.collectionName
        binding.changeableYear.text = trackModel.releaseDate.substring(0, 4)
        binding.changeableGenre.text = trackModel.primaryGenreName
        binding.changeableCountry.text = trackModel.country
    }
    
    private fun updatePlayButton(imageResource: Int) {
        binding.playButton.setImageResource(imageResource)
    }
    
    private fun updateTrackDuration(currentPositionMediaPlayer: Int) {
        binding.excerptDuration.text = currentPositionMediaPlayer.millisConverter()
    }
    
    private fun initListeners() {
        binding.navigationToolbar.setNavigationOnClickListener {
            navigationRouter.goBack()
        }
        
        binding.playButton.setOnClickListener {
            binding.playButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale))
            viewModel.playButtonClicked()
        }
    }
}
