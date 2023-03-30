package com.practicum.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.api.TrackData
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_DATA = "track_data"
        private const val DELAY = 1000L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var url: String
    private lateinit var backButton: androidx.appcompat.widget.Toolbar
    private lateinit var addButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var excerptDuration: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player_activity)

        viewSetting()
        preparePlayer()
        settingListeners()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    private fun viewSetting() {
        val coverImage = findViewById<ImageView>(R.id.cover)
        val trackName = findViewById<TextView>(R.id.track_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val duration = findViewById<TextView>(R.id.changeable_duration)
        val albumName = findViewById<TextView>(R.id.changeable_album)
        val year = findViewById<TextView>(R.id.changeable_year)
        val genre = findViewById<TextView>(R.id.changeable_genre)
        val country = findViewById<TextView>(R.id.changeable_country)
        backButton = findViewById(R.id.navigation_toolbar)
        addButton = findViewById(R.id.add_button)
        playButton = findViewById(R.id.play_button)
        likeButton = findViewById(R.id.like_button)
        excerptDuration = findViewById(R.id.excerpt_duration)

        val track: TrackData =
            Gson().fromJson((intent.getStringExtra(TRACK_DATA)), TrackData::class.java)
        url = track.previewUrl

        val cornerRadius = this.resources.getDimensionPixelSize(R.dimen.corner_radius_8dp)

        Glide.with(this).load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder).centerCrop()
            .transform(RoundedCorners(cornerRadius)).into(coverImage)

        excerptDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        albumName.text = track.collectionName
        year.text = track.releaseDate.substring(0, 4)
        genre.text = track.primaryGenreName
        country.text = track.country

    }

    private fun settingListeners() {
        backButton.setNavigationOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale))
            playbackControl()

        }
    }

    private fun preparePlayer() {
        mediaPlayer.apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            setOnCompletionListener {
                playButton.setImageResource(R.drawable.play_button)
                handler.removeCallbacksAndMessages(null)
                changeDuration(0)
                playerState = STATE_PREPARED
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playButton.setImageResource(R.drawable.pause_button)
        handler.postDelayed(object : Runnable {
            override fun run() {
                changeDuration(mediaPlayer.currentPosition)
                handler.postDelayed(this, DELAY)
            }
        }, DELAY)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playButton.setImageResource(R.drawable.play_button)
        handler.removeCallbacksAndMessages(null)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun changeDuration(milliseconds: Int) {
        excerptDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(milliseconds)
    }

}
