package com.practicum.playlistmaker.player.ui.fragment

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.root.InternetConnectionBroadcastReceiver
import com.practicum.playlistmaker.core.utils.millisConverter
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.library.ui.bottom_sheet.BottomSheetPlaylists
import com.practicum.playlistmaker.player.ui.models.PlayStatus
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player), KoinComponent {

    private val binding by viewBinding<FragmentAudioPlayerBinding>()
    private val viewModel by viewModel<AudioPlayerViewModel>()
    private val broadcastReceiver: InternetConnectionBroadcastReceiver by inject()
    private var track: TrackModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = requireArguments()
            .getString(KEY_TRACK)
            ?.let { Json.decodeFromString<TrackModel>(it) } ?: TrackModel.emptyTrack

        track?.let {
            viewModel.preparingPlayer(it.previewUrl)
            viewModel.isFavorite(it.trackId)
            drawTrack(it)
        }

        initObserver()
        initListeners()
    }

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            requireContext(),
            broadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(broadcastReceiver)
        viewModel.onViewPaused()
    }

    private fun drawTrack(trackModel: TrackModel) {

        val cornerRadius =
            requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_8dp)

        with(binding) {
            ivCover.setImage(
                url = trackModel.previewUrl512,
                placeholder = R.drawable.placeholder,
                cornerRadius = cornerRadius,
            )
            tvExcerptDuration.text = AudioPlayerViewModel.START_POSITION.millisConverter()
            tvTrackName.text = trackModel.trackName
            tvArtistName.text = trackModel.artistName
            tvChangeableDuration.text = trackModel.trackTimeMillis.millisConverter()
            tvChangeableAlbum.text = trackModel.collectionName
            tvChangeableYear.text = try {
                trackModel.releaseDate.substring(0, 4)
            } catch (e: StringIndexOutOfBoundsException) {
                ""
            }
            tvChangeableGenre.text = trackModel.primaryGenreName
            tvChangeableCountry.text = trackModel.country
        }
    }

    private fun initObserver() {
        with(viewModel) {
            isFavoriteStatus.observe(viewLifecycleOwner) { isFavorite ->
                renderLikeButton(isFavorite)
            }
            playStatus.observe(viewLifecycleOwner) { playingStatus ->
                renderPlayingContent(playingStatus)
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            tbNavigation.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            playButton.setOnClickListener { button ->
                button?.let { startAnimation(it) }
                track?.let { viewModel.playButtonClicked(it.previewUrl) }
            }

            likeButton.setOnClickListener { button ->
                button?.let { startAnimation(it) }
                track?.let { viewModel.toggleFavorite(it) }
            }

            addButton.setOnClickListener { button ->
                button?.let { startAnimation(it) }
                findNavController().navigate(R.id.action_audioPlayerFragment_to_bottomSheetPlaylists,
                    track?.let {
                        BottomSheetPlaylists.createArgs(it)
                    })
            }
        }
    }

    private fun renderLikeButton(isFavorite: Boolean) {
        val imageResource = if (isFavorite) R.drawable.button_liked
        else R.drawable.button_unliked

        binding.likeButton.setImageResource(imageResource)
    }

    private fun renderPlayingContent(status: PlayStatus) {
        when (status) {
            is PlayStatus.NotConnected, is PlayStatus.Loading -> {
                showMessage(status)
            }

            is PlayStatus.Playing -> {
                startAnimation(binding.playButton)
                renderPlayPosition(status.playProgress)
            }

            is PlayStatus.Paused -> {
                renderPlayPosition(status.playProgress)
            }

            is PlayStatus.Ready -> {
                renderPlayPosition(status.playProgress)
                binding.playButton.setInitStatus()
            }
        }
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

    private fun startAnimation(button: View) {
        button.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(), R.anim.scale
            )
        )
    }

    private fun renderPlayPosition(currentPositionMediaPlayer: Int) {
        binding.tvExcerptDuration.text = currentPositionMediaPlayer.millisConverter()
    }

    companion object {
        const val KEY_TRACK = "track"

        fun createArgs(track: TrackModel): Bundle = bundleOf(
            KEY_TRACK to Json.encodeToString(track)
        )
    }
}
