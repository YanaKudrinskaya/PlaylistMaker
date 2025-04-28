package com.yanakudrinskaya.playlistmaker.player.ui.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.yanakudrinskaya.playlistmaker.utils.AppUtils
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.yanakudrinskaya.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.yanakudrinskaya.playlistmaker.player.ui.model.TrackScreenState
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private val viewModel by viewModels<AudioPlayerViewModel> {AudioPlayerViewModel.getViewModelFactory() }
    private lateinit var binding: ActivityAudioPlayerBinding

    private val typedValuePlay = TypedValue()
    private val typedValuePause = TypedValue()
    private lateinit var playIcon : Drawable
    private lateinit var pauseIcon : Drawable

    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupIcons()
        setupClickListeners()
        setupObservers()
    }

    private fun setupIcons() {
        theme.resolveAttribute(R.attr.playButton, typedValuePlay, true)
        theme.resolveAttribute(R.attr.pauseButton, typedValuePause, true)
        playIcon = AppCompatResources.getDrawable(this, typedValuePlay.resourceId)!!
        pauseIcon = AppCompatResources.getDrawable(this, typedValuePause.resourceId)!!
    }

    private fun setupClickListeners() {

        binding.buttonPlay.setOnClickListener {
            if (viewModel.getPlayStatusLiveData().value?.isPlaying == true) {
                viewModel.pause()
            } else {
                viewModel.play()
            }
        }

        binding.toolbarAudioPlayer.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is TrackScreenState.Content -> updateUI(screenState.trackModel)
                is TrackScreenState.Loading -> changeContentVisibility(loading = true)
            }
        }

        viewModel.getPlayStatusLiveData().observe(this) { playStatus ->
            if(playStatus.isPlaying != isPlaying) updatePlayButton(playStatus.isPlaying)
            binding.time.text = playStatus.progress
        }

    }

    private fun updateUI(track: Track) {
        changeContentVisibility(loading = false)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(AppUtils.dpToPx(8f, this)))
            .into(binding.trackImage)

        binding.collectionNameGroup.visibility = if(track.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        binding.collectionNameValue.text = track.collectionName
        binding.releaseDateValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        binding.primaryGenreNameValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
    }

    private fun changeContentVisibility(loading: Boolean) {
        binding.progressBar.isVisible = loading
        binding.contentGroup.isVisible = !loading
    }

    private fun updatePlayButton(status: Boolean) {
        if(status) {
            binding.buttonPlay.setImageDrawable(pauseIcon)
        } else {
            binding.buttonPlay.setImageDrawable(playIcon)
        }
        isPlaying = status
    }
}