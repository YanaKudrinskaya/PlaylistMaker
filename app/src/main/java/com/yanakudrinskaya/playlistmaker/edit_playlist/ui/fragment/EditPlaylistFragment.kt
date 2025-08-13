package com.yanakudrinskaya.playlistmaker.edit_playlist.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.text.TextWatcher
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.create_playlist.ui.fragment.CreatePlaylistFragment
import com.yanakudrinskaya.playlistmaker.edit_playlist.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by viewModel(parameters = {
        val args = EditPlaylistFragmentArgs.fromBundle(requireArguments())
        parametersOf(args.playlistId)
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tbNewPlaylist.title = getString(R.string.edit_playlist)
        binding.tvBtnCreate.text = getString(R.string.save)
        binding.tvBtnCreate.isEnabled = true

        viewModel.getInitialPlaylist().observe(viewLifecycleOwner) { playlist ->
            binding.etTitle.setText(playlist.title)
            binding.etDescription.setText(playlist.description)

            if (playlist.cover.isNotEmpty()) {
                Glide.with(this)
                    .load(File(playlist.cover))
                    .into(binding.ivArtwork)
            }
        }

        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvBtnCreate.isEnabled = !s.isNullOrEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun handleBackNavigation() {
        findNavController().navigateUp()
    }
}