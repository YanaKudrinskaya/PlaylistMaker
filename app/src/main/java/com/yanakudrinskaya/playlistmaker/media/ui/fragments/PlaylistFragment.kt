package com.yanakudrinskaya.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yanakudrinskaya.playlistmaker.databinding.FragmentPlaylistBinding
import com.yanakudrinskaya.playlistmaker.media.ui.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistFragment : Fragment() {

    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

        fun newInstance() = PlaylistFragment().apply {
            arguments = Bundle().apply {}
        }
    }
}