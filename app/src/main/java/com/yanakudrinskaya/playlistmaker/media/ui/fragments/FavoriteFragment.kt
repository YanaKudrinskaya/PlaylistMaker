package com.yanakudrinskaya.playlistmaker.media.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.playlistmaker.databinding.FragmentFavoriteBinding
import com.yanakudrinskaya.playlistmaker.media.ui.model.FavoriteScreenState
import com.yanakudrinskaya.playlistmaker.media.ui.view_model.FavoriteViewModel
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.search.ui.TrackListAdapter
import com.yanakudrinskaya.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoriteFragment : Fragment() {

    private val viewModel by viewModel<FavoriteViewModel>()

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val trackListAdapter = TrackListAdapter()

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMediaTracklist.adapter = trackListAdapter

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            val direction = MediaFragmentDirections.actionMediaFragmentToAudioPlayerFragment(track)
            findNavController().navigate(direction)
        }

        setupListeners()
        setupObserves()
    }

    private fun setupObserves() {
        viewModel.getFavoriteStateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun setupListeners() {
        trackListAdapter.onItemClick = { track -> onTrackClickDebounce(track) }

    }

    private fun render(state: FavoriteScreenState) {
        when (state) {
            is FavoriteScreenState.Content -> showContent(state.tracks)
            is FavoriteScreenState.Empty -> showEmptyMessage(true)
        }
    }

    private fun showEmptyMessage(isVisible: Boolean) {
        binding.favoriteEmpty.isVisible = isVisible
        binding.rvMediaTracklist.isVisible = !isVisible
    }

    private fun showContent(list: List<Track>) {
        showEmptyMessage(false)
        trackListAdapter.removeItems()
        trackListAdapter.trackList.addAll(list)
        trackListAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }
}