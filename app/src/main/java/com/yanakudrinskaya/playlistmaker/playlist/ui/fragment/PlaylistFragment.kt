package com.yanakudrinskaya.playlistmaker.playlist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.databinding.FragmentPlaylistBinding
import com.yanakudrinskaya.playlistmaker.playlist.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.playlist.ui.PlaylistAdapter
import com.yanakudrinskaya.playlistmaker.playlist.ui.models.PlaylistScreenState
import com.yanakudrinskaya.playlistmaker.playlist.ui.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private val viewModel by viewModel<PlaylistViewModel>()

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val playlistAdapter = PlaylistAdapter()

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

        binding.rvPlaylist.adapter = playlistAdapter

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
            findNavController().navigate(R.id.action_mediaFragment_to_creatPlaylistFragment)
        }
    }

    private fun render(state: PlaylistScreenState) {
        when (state) {
            is PlaylistScreenState.Content -> showContent(state.playlists)
            is PlaylistScreenState.Empty -> showEmptyMessage(true)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance() = PlaylistFragment().apply {
            arguments = Bundle().apply {}
        }
    }
}