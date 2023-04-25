package com.practicum.playlistmaker.presentation.ui.audioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.models.TrackModel
import com.practicum.playlistmaker.presentation.presenters.audioplayer.AudioPlayerPresenter
import com.practicum.playlistmaker.presentation.presenters.audioplayer.AudioPlayerView
import com.practicum.playlistmaker.presentation.presenters.router.ToolsRouter

class AudioPlayerActivity : AppCompatActivity(), AudioPlayerView {

    private lateinit var navigationButton: androidx.appcompat.widget.Toolbar
    private lateinit var addLibraryButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var excerptDuration: TextView
    private lateinit var coverImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var albumName: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var presenter: AudioPlayerPresenter

    private val router = ToolsRouter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player_activity)

        initViews()
        initListeners()
        presenter = Creator.provideAudioPlayerPresenter(view = this, activity = this)
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroyed()
    }

    override fun drawTrack(trackModel: TrackModel, startPosition: Int) {

        val cornerRadius = this.resources.getDimensionPixelSize(R.dimen.corner_radius_8dp)

        router.glideProvider(
            context = this,
            url = trackModel.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"),
            placeholder = R.drawable.placeholder,
            cornerRadius = cornerRadius,
            view = coverImage
        )

        excerptDuration.text = router.millisConverter(millis = startPosition)
        trackName.text = trackModel.trackName
        artistName.text = trackModel.artistName
        duration.text = router.millisConverter(millis = trackModel.trackTimeMillis)
        albumName.text = trackModel.collectionName
        year.text = trackModel.releaseDate.substring(0, 4)
        genre.text = trackModel.primaryGenreName
        country.text = trackModel.country
    }

    override fun updatePlayButton(imageResource: Int) {
        playButton.setImageResource(imageResource)
    }

    override fun updateTrackDuration(currentPositionMediaPlayer: Int) {
        excerptDuration.text =
            router.millisConverter(millis = currentPositionMediaPlayer)
    }

    private fun initViews() {
        coverImage = findViewById(R.id.cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        duration = findViewById(R.id.changeable_duration)
        albumName = findViewById(R.id.changeable_album)
        year = findViewById(R.id.changeable_year)
        genre = findViewById(R.id.changeable_genre)
        country = findViewById(R.id.changeable_country)
        navigationButton = findViewById(R.id.navigation_toolbar)
        addLibraryButton = findViewById(R.id.add_button)
        playButton = findViewById(R.id.play_button)
        likeButton = findViewById(R.id.like_button)
        excerptDuration = findViewById(R.id.excerpt_duration)
    }

    private fun initListeners() {
        navigationButton.setNavigationOnClickListener {
            presenter.backButtonClicked()
        }

        playButton.setOnClickListener {
            playButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale))
            presenter.playButtonClicked()
        }
    }
}
