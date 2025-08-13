package com.yanakudrinskaya.playlistmaker.playlists.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.databinding.FragmentPlaylistsBinding
import com.yanakudrinskaya.playlistmaker.media.ui.fragment.MediaFragmentDirections
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.playlists.ui.PlaylistAdapter
import com.yanakudrinskaya.playlistmaker.playlists.ui.models.PlaylistsScreenState
import com.yanakudrinskaya.playlistmaker.playlists.ui.view_model.PlaylistsViewModel
import com.yanakudrinskaya.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel by viewModel<PlaylistsViewModel>()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val playlistAdapter = PlaylistAdapter()
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPlaylist.adapter = playlistAdapter

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            val direction = MediaFragmentDirections.actionMediaFragmentToPlaylistFragment(playlist.id)
            findNavController().navigate(direction)
        }

        setupListeners()
        setupObserves()
    }

    private fun setupObserves() {
        viewModel.getPlaylistStateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun setupListeners() {

        binding.addPlaylistBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

        playlistAdapter.onItemClick = { playlist -> onPlaylistClickDebounce(playlist) }
    }

    private fun render(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.Content -> showContent(state.playlists)
            is PlaylistsScreenState.Empty -> showEmptyMessage(true)
        }
    }

    private fun showEmptyMessage(isVisible: Boolean) {
        binding.playlistEmpty.isVisible = isVisible
        binding.rvPlaylist.isVisible = !isVisible
    }

    private fun showContent(list: List<Playlist>) {
        showEmptyMessage(false)
        playlistAdapter.removeItems()
        playlistAdapter.playlists.addAll(list)
        playlistAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylist()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = PlaylistsFragment().apply {
            arguments = Bundle().apply {}
        }

    }
}