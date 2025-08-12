package com.yanakudrinskaya.playlistmaker.create_playlist.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yanakudrinskaya.playlistmaker.create_playlist.ui.models.CreationState
import com.yanakudrinskaya.playlistmaker.create_playlist.ui.view_model.CreatePlaylistViewModel
import com.yanakudrinskaya.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.yanakudrinskaya.playlistmaker.root.ui.NavigationVisibilityController
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.getValue

open class CreatePlaylistFragment : Fragment() {

    open val viewModel by viewModel<CreatePlaylistViewModel>()

    private var _binding: FragmentCreatePlaylistBinding? = null
    open val binding get() = _binding!!

    private lateinit var titleSimpleTextWatcher: TextWatcher
    private lateinit var descriptionSimpleTextWatcher: TextWatcher
    private var title: String = ""
    private var description: String = ""
    private var coverIsSelected: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(false)
    }

    private fun setupObservers() {

        viewModel.getCoverState().observe(viewLifecycleOwner) { cover ->
            cover?.let {
                coverIsSelected = true
                Glide.with(this)
                    .load(File(it.filePath))
                    .centerCrop()
                    .into(binding.ivArtwork)
            }
        }

        viewModel.getCreationState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is CreationState.Success -> {
                    findNavController().navigateUp()
                    showMessage(state.message)
                }

                is CreationState.Error -> {
                    showMessage(state.message)
                }
            }
        }
    }

    private fun setupListeners() {

        binding.tbNewPlaylist.setNavigationOnClickListener {
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

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let { viewModel.saveCover(it) }
            }

        binding.ivArtwork.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.tvBtnCreate.setOnClickListener {
            viewModel.createPlaylist(title, description)
        }

        titleSimpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvBtnCreate.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                title = s?.toString() ?: ""
            }
        }

        descriptionSimpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                description = s?.toString() ?: ""
            }
        }

        titleSimpleTextWatcher.let { binding.etTitle.addTextChangedListener(it) }
        descriptionSimpleTextWatcher.let { binding.etDescription.addTextChangedListener(it) }
    }

    open fun handleBackNavigation() {
        if (hasUnsavedChanges()) {
            showDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        return title.isNotEmpty() || description.isNotEmpty() || coverIsSelected
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showDialog() {
        val dialogText = viewModel.getDialogText()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(dialogText.title)
            .setMessage(dialogText.message)
            .setPositiveButton(dialogText.positiveButton) { dialog, which ->
                findNavController().navigateUp()
            }
            .setNeutralButton(dialogText.neutralButton) { dialog, which ->

            }
            .show()
    }

    override fun onDestroyView() {
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(true)
        super.onDestroyView()
        _binding = null
    }
}


