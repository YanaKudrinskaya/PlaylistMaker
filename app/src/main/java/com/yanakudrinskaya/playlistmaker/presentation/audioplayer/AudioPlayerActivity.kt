package com.yanakudrinskaya.playlistmaker.presentation.audioplayer

import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.yanakudrinskaya.playlistmaker.AppUtils
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var  track: Track
    private var mediaPlayer = MediaPlayer()
    private lateinit var toolbar: Toolbar
    private lateinit var trackImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var buttonPlay: ImageButton
    private lateinit var buttonCopy: ImageButton
    private lateinit var buttonLike: ImageButton
    private lateinit var time: TextView
    private lateinit var duration: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var collectionNameGroup: Group
    private val typedValuePlay = TypedValue()
    private val typedValuePause = TypedValue()
    private lateinit var playIcon : Drawable
    private lateinit var pauseIcon : Drawable

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 1000L
    }

    private var playerState = STATE_DEFAULT
    private var mainThreadHandler: Handler? = null
    private var isTimerPaused = false
    private var pausedTimeRemaining = 0L
    private var timerRunnable: Runnable? = null
    private var startTime = 0L
    private var trackDuration = 30000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audio_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        theme.resolveAttribute(R.attr.playButton, typedValuePlay, true)
        theme.resolveAttribute(R.attr.pauseButton, typedValuePause, true)
        playIcon = AppCompatResources.getDrawable(this, typedValuePlay.resourceId)!!
        pauseIcon = AppCompatResources.getDrawable(this, typedValuePause.resourceId)!!


        toolbar = findViewById(R.id.toolbar_audio_player)
        trackImage = findViewById(R.id.track_image)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        buttonPlay = findViewById(R.id.button_play)
        buttonCopy = findViewById(R.id.button_copy)
        buttonLike = findViewById(R.id.button_like)
        time = findViewById(R.id.time)
        duration = findViewById(R.id.duration_value)
        collectionName = findViewById(R.id.collection_name_value)
        releaseDate = findViewById(R.id.release_date_value)
        primaryGenreName = findViewById(R.id.primary_genre_name_value)
        country = findViewById(R.id.country_value)
        collectionNameGroup = findViewById(R.id.collection_name_group)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        track = intent.getSerializableExtra("track") as Track

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(AppUtils.dpToPx(8f, this)))
            .into(trackImage)

        collectionNameGroup.visibility = if(track.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE

        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        collectionName.text = track.collectionName
        releaseDate.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        preparePlayer()

        mainThreadHandler = Handler(Looper.getMainLooper())

        buttonPlay.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        pauseTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        if (track.previewUrl!=null) {
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                buttonPlay.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
            }
        } else Log.d("Player", "Такой трек нельзя послушать")

    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        startTimer()
        buttonPlay.setImageDrawable(pauseIcon)
        playerState = STATE_PLAYING

    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        buttonPlay.setImageDrawable(playIcon)
        playerState = STATE_PAUSED
        pauseTimer()
    }

    private fun startTimer() {
        if (timerRunnable == null) {
            startTime = System.currentTimeMillis()
            timerRunnable = createUpdateTimerTask()
            mainThreadHandler?.post(timerRunnable!!)
        } else if (isTimerPaused) {
            resumeTimer()
        }
    }

    private fun pauseTimer() {
        if (timerRunnable != null && !isTimerPaused) {
            isTimerPaused = true
            mainThreadHandler?.removeCallbacks(timerRunnable!!)
            pausedTimeRemaining = System.currentTimeMillis() - startTime
        }
    }

    private fun resumeTimer() {
        if (isTimerPaused && timerRunnable != null) {
            isTimerPaused = false
            startTime = System.currentTimeMillis() - pausedTimeRemaining
            mainThreadHandler?.post(timerRunnable!!)
        }
    }

    private fun stopTimer() {
        timerRunnable?.let {
            mainThreadHandler?.removeCallbacks(it)
        }
        timerRunnable = null
        isTimerPaused = false
        pausedTimeRemaining = 0L
        time.text ="0:00"
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (!isTimerPaused) {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    val seconds = elapsedTime / DELAY
                    if (elapsedTime < trackDuration) {
                        time.text = String.format("%d:%02d", seconds / 60, seconds % 60)
                        mainThreadHandler?.postDelayed(this, DELAY)
                    } else {
                        stopTimer()
                        pausePlayer()
                    }
                }
            }
        }
    }
}