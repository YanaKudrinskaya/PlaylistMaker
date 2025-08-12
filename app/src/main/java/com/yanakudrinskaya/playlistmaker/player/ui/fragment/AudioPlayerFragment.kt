package com.yanakudrinskaya.playlistmaker.player.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yanakudrinskaya.playlistmaker.player.ui.BottomPlaylistAdapter
import com.yanakudrinskaya.playlistmaker.player.ui.model.BottomSheetState
import com.yanakudrinskaya.playlistmaker.player.ui.model.PlayStatus
import com.yanakudrinskaya.playlistmaker.player.ui.model.ToastState
import com.yanakudrinskaya.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.playlists.ui.models.PlaylistsScreenState
import com.yanakudrinskaya.playlistmaker.root.ui.NavigationVisibilityController
import com.yanakudrinskaya.playlistmaker.root.ui.activity.RootActivity
import kotlinx.coroutines.launch
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

    private val playlistAdapter = BottomPlaylistAdapter()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(false)

        binding.rvPlaylist.adapter = playlistAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }


        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> viewModel.hideBottomSheet()
                    BottomSheetBehavior.STATE_COLLAPSED -> viewModel.showBottomSheet()
                    BottomSheetBehavior.STATE_EXPANDED -> viewModel.showBottomSheet()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

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

        binding.buttonPlaylist.setOnClickListener {
            viewModel.showBottomSheet()
        }

        binding.addPlaylistBtn.setOnClickListener {
            viewModel.hideBottomSheet()
            findNavController().navigate(R.id.action_audioPlayerFragment_to_creatPlaylistFragment)

        }

        playlistAdapter.onItemClick = { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }
    }

    private fun setupObservers() {

        viewModel.getToastMessage().observe(viewLifecycleOwner) {state ->
            when(state) {
                is ToastState.DontShow -> {}
                is ToastState.Show -> {
                    if(state.trackAdded) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewModel.clearToast()
                }
            }
        }

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

        viewModel.getBottomPlaylistStateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBottomSheetState().collect { state ->
                    when (state) {
                        BottomSheetState.HIDDEN -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                            binding.overlay.visibility = View.GONE
                        }
                        BottomSheetState.COLLAPSED -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            binding.overlay.visibility = View.VISIBLE
                        }
                        BottomSheetState.EXPANDED -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            binding.overlay.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

    }

    private fun render(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.Content -> showContent(state.playlists)
            is PlaylistsScreenState.Empty -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? RootActivity)?.viewModel?.setNavigationVisible(false)
    }

    private fun setupFavoriteIcon(isFavorite: Boolean) {
        binding.buttonFavorite.setImageDrawable(if (isFavorite) likedIcon else likeIcon)
    }

    private fun showContent(list: List<Playlist>) {
        playlistAdapter.removeItems()
        playlistAdapter.playlists.addAll(list)
        playlistAdapter.notifyDataSetChanged()
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
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(true)
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}


