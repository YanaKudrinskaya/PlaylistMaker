package com.yanakudrinskaya.playlistmaker.player.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.yanakudrinskaya.playlistmaker.player.ui.model.TrackScreenState
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.utils.AppUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.yanakudrinskaya.playlistmaker.player.ui.model.PlayStatus
import com.yanakudrinskaya.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<AudioPlayerFragmentArgs>()

    private val typedValuePlay = TypedValue()
    private val typedValuePause = TypedValue()
    private lateinit var playIcon: Drawable
    private lateinit var pauseIcon: Drawable

    private lateinit var likeIcon: Drawable
    private lateinit var likedIcon: Drawable

    private var isPlaying = false
    private val viewModel by viewModel<AudioPlayerViewModel> {
        parametersOf(args.track)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupIcons()
        setupClickListeners()
        setupObservers()
    }

    private fun setupIcons() {
        requireContext().theme.resolveAttribute(R.attr.playButton, typedValuePlay, true)
        requireContext().theme.resolveAttribute(R.attr.pauseButton, typedValuePause, true)

        playIcon = AppCompatResources.getDrawable(requireContext(), typedValuePlay.resourceId)!!
        pauseIcon = AppCompatResources.getDrawable(requireContext(), typedValuePause.resourceId)!!

        likeIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.like)!!
        likedIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.liked)!!
    }

    private fun setupClickListeners() {

        binding.buttonPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.toolbarAudioPlayer.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonFavorite.setOnClickListener {
            viewModel.onFavoriteButtonClicked()
        }
    }

    private fun setupObservers() {

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is TrackScreenState.Content -> updateUI(screenState.trackModel)
                is TrackScreenState.Loading -> changeContentVisibility(loading = true)
                is TrackScreenState.Favorite -> setupFavoriteIcon(screenState.isFavorite)
            }
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.buttonPlay.isEnabled = it.isPlayButtonEnabled
            when (it.status) {
                PlayStatus.PLAY -> updatePlayButton(false)
                PlayStatus.PAUSE -> updatePlayButton(true)
            }
            binding.time.text = it.progress
        }

    }

    private fun setupFavoriteIcon(isFavorite: Boolean) {
        binding.buttonFavorite.setImageDrawable(if (isFavorite) likedIcon else likeIcon)
    }

    private fun updateUI(track: Track) {
        changeContentVisibility(loading = false)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(AppUtils.dpToPx(8f, requireContext())))
            .into(binding.trackImage)

        setupFavoriteIcon(track.isFavorite)
        binding.collectionNameGroup.visibility =
            if (track.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.durationValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        binding.collectionNameValue.text = track.collectionName
        binding.releaseDateValue.text =
            SimpleDateFormat("yyyy", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        binding.primaryGenreNameValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
    }

    private fun changeContentVisibility(loading: Boolean) {
        binding.progressBar.isVisible = loading
        binding.contentGroup.isVisible = !loading
    }

    private fun updatePlayButton(status: Boolean) {
        if (status) {
            binding.buttonPlay.setImageDrawable(pauseIcon)
        } else {
            binding.buttonPlay.setImageDrawable(playIcon)
        }
        isPlaying = status
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

}