package com.practicum.playlistmaker.player.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.root.InternetConnectionBroadcastReceiver
import com.practicum.playlistmaker.core.utils.millisConverter
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.library.ui.bottom_sheet.BottomSheetPlaylists
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.service.PlayerService
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.playlist_creator.domain.models.PermissionResultState
import com.practicum.playlistmaker.search.domain.models.TrackModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.AudioServiceBinder
            viewModel.initPlayerControl(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removePlayerControl()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = requireArguments()
            .getString(KEY_TRACK)
            ?.let { Json.decodeFromString<TrackModel>(it) } ?: TrackModel.emptyTrack

        track?.let {
            viewModel.isFavorite(it.trackId)
            drawTrack(it)
        }

        initObserver()
        initListeners()
        bindMusicService()
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

        viewModel.onViewResumed()
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(broadcastReceiver)
        viewModel.onViewPaused()
    }

    override fun onDestroyView() {
        unbindMusicService()
        super.onDestroyView()
    }

    private fun drawTrack(trackModel: TrackModel) {

        val cornerRadius =
            requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_8dp)

        with(binding) {
            ivCover.setImage(
                url = trackModel.artworkUrl512,
                placeholder = R.drawable.placeholder,
                cornerRadius = cornerRadius,
            )
            tvExcerptDuration.text = PlayerState.START_POSITION.millisConverter()
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

            observePlayerState()
                .onEach(::renderPlayingContent)
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .launchIn(viewLifecycleOwner.lifecycleScope)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                observePermissionState()
                    .onEach(::renderPermissions)
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            tbNavigation.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            playButton.setOnClickListener { button ->
                button?.let(::startAnimation)
                viewModel.onPlayerButtonClicked()
            }

            likeButton.setOnClickListener { button ->
                button?.let(::startAnimation)
                track?.let(viewModel::toggleFavorite)
            }

            addButton.setOnClickListener { button ->
                button?.let(::startAnimation)
                findNavController().navigate(
                    R.id.action_audioPlayerFragment_to_bottomSheetPlaylists,
                    track?.let(BottomSheetPlaylists::createArgs)
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderPermissions(state: PermissionResultState) {
        when (state) {
            PermissionResultState.NEEDS_RATIONALE -> showPermissionMessage()
            PermissionResultState.DENIED_PERMANENTLY -> {
                showPermissionMessage()
                binding.playButton.setInitStatus()
                openSettings()
            }
            PermissionResultState.GRANTED -> Unit
        }
    }

    private fun renderLikeButton(isFavorite: Boolean) {
        val imageResource = if (isFavorite) R.drawable.button_liked
        else R.drawable.button_unliked

        binding.likeButton.setImageResource(imageResource)
    }

    private fun renderPlayingContent(state: PlayerState) {
        binding.playButton.isEnabled = state.buttonState
        binding.tvExcerptDuration.text = state.progress.millisConverter()

        when (state) {
            is PlayerState.Playing -> startAnimation(binding.playButton)
            is PlayerState.Prepared -> binding.playButton.setInitStatus()
            else -> Unit
        }
    }

    private fun startAnimation(button: View) {
        button.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(), R.anim.scale
            )
        )
    }

    private fun showPermissionMessage() {
        Toast.makeText(
            requireContext(),
            getString(R.string.permission_player_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.fromParts("package", requireContext().packageName, null)
        requireContext().startActivity(intent)
    }

    private fun bindMusicService() {
        val description = StringBuilder()
        description.append(track?.artistName)
        description.append(" - ")
        description.append(track?.trackName)

        val intent = Intent(requireContext(), PlayerService::class.java).apply {
            putExtra(KEY_URL, track?.previewUrl)
            putExtra(KEY_DESCRIPTION, description.toString())
        }

        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindMusicService() {
        requireActivity().unbindService(serviceConnection)
    }

    companion object {
        const val KEY_TRACK = "track"
        const val KEY_URL = "song_url"
        const val KEY_DESCRIPTION = "song_description"

        fun createArgs(track: TrackModel): Bundle = bundleOf(
            KEY_TRACK to Json.encodeToString(track)
        )
    }
}
