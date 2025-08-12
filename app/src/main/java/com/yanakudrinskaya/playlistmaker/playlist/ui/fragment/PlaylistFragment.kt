package com.yanakudrinskaya.playlistmaker.playlist.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.databinding.FragmentPlaylistBinding
import com.yanakudrinskaya.playlistmaker.favorite.ui.fragment.FavoriteFragment
import com.yanakudrinskaya.playlistmaker.media.ui.fragment.MediaFragment
import com.yanakudrinskaya.playlistmaker.media.ui.fragment.MediaFragmentDirections
import com.yanakudrinskaya.playlistmaker.player.ui.model.BottomSheetState
import com.yanakudrinskaya.playlistmaker.playlist.ui.models.PlaylistScreenState
import com.yanakudrinskaya.playlistmaker.playlist.ui.view_model.PlaylistViewModel
import com.yanakudrinskaya.playlistmaker.root.ui.NavigationVisibilityController
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.search.ui.TrackListAdapter
import com.yanakudrinskaya.playlistmaker.search.ui.fragment.SearchFragmentDirections
import com.yanakudrinskaya.playlistmaker.utils.AppUtils
import com.yanakudrinskaya.playlistmaker.utils.debounce
import com.yanakudrinskaya.playlistmaker.utils.formatTrackCount
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import kotlin.getValue

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<PlaylistFragmentArgs>()

    private val viewModel by viewModel<PlaylistViewModel> {
        parametersOf(args.playlistId)
    }

    private val trackListAdapter = TrackListAdapter()
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvTracks.adapter = trackListAdapter

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            val direction =
                PlaylistFragmentDirections.actionPlaylistFragmentToAudioPlayerFragment(track)
            findNavController().navigate(direction)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.moreBottomSheet).apply {
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

        setupListeners()
        setupObserves()
    }

    private fun setupObserves() {
        viewModel.getPlaylistStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistScreenState.Content -> {
                    showContent(state)
                }

                is PlaylistScreenState.EmptyList -> Toast.makeText(
                    requireContext(),
                    state.message,
                    Toast.LENGTH_SHORT
                ).show()

                is PlaylistScreenState.Share -> startActivity(state.intent)
            }
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

        viewModel.playlistDeleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted) {
                findNavController().navigateUp()
            }
        }
    }


    private fun showContent(content: PlaylistScreenState.Content) {
        Glide.with(this)
            .load(File(content.coverUrl))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.ivCover)

        binding.tvTitle.text = content.title
        binding.tvDescription.text = content.description
        binding.tvTreckCount.text = content.tracksCount
        binding.tvDuration.text = content.duration


        if (content.tracks.isEmpty()) {
            showEmptyMessage(true)
        } else {
            showEmptyMessage(false)
            trackListAdapter.trackList = content.tracks.toMutableList()
            trackListAdapter.notifyDataSetChanged()
        }

        Glide.with(this)
            .load(File(content.coverUrl))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(AppUtils.dpToPx(8f, requireContext())))
            .into(binding.ivCoverBottom)
        binding.tvTitleBottom.text = content.title
        binding.tvTreckCountBottom.text = formatTrackCount(content.tracks.size)
    }

    private fun showEmptyMessage(isVisible: Boolean) {
        binding.tvEmpty.isVisible = isVisible
        binding.rvTracks.isVisible = !isVisible
    }

    private fun setupListeners() {

        binding.tbPlaylist.setNavigationOnClickListener {
            handleBackNavigation()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackNavigation()
                }
            }
        )

        trackListAdapter.onItemClick = { track -> onTrackClickDebounce(track) }

        trackListAdapter.onLongItemClick = { track ->
            showDeleteTrackDialog(track)
        }

        binding.ibShare.setOnClickListener {
            viewModel.sharing()
        }

        binding.ibMore.setOnClickListener {
            viewModel.showBottomSheet()
        }

        binding.tvSharing.setOnClickListener {
            viewModel.sharing()
        }

        binding.tvEdit.setOnClickListener {
            val direction = PlaylistFragmentDirections.actionPlaylistFragmentToEditPlaylistFragment(args.playlistId)
            findNavController().navigate(direction)
        }

        binding.tvDelete.setOnClickListener {
            viewModel.hideBottomSheet()
            showDeletePlaylistDialog()
        }
    }

    private fun showDeletePlaylistDialog() {
        val playlistName = viewModel.currentPlaylist.title
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Вы уверены, что хотите удалить \"$playlistName\"?")
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Удалить") { dialog, _ ->
                dialog.dismiss()
                viewModel.deletePlaylist()
            }
            .create()
            .show()
    }

    override fun onResume() {
        super.onResume()
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(false)
    }

    private fun showDeleteTrackDialog(track: Track) {
        AlertDialog.Builder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setNegativeButton("НЕТ") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("ДА") { dialog, _ ->
                dialog.dismiss()
                deleteTrackFromPlaylist(track)
            }
            .create()
            .show()
    }

    private fun handleBackNavigation() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(true)
        super.onDestroyView()
        _binding = null
    }

    private fun deleteTrackFromPlaylist(track: Track) {
        val updatedTracks = trackListAdapter.trackList.toMutableList().apply {
            remove(track)
        }
        viewModel.updatePlaylistTracks(updatedTracks.toList())
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}