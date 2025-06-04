package com.yanakudrinskaya.playlistmaker.settings.ui.fragment

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yanakudrinskaya.playlistmaker.databinding.FragmentSettingsBinding
import com.yanakudrinskaya.playlistmaker.settings.ui.model.NavigationEvent
import com.yanakudrinskaya.playlistmaker.settings.ui.model.SettingsEvent
import com.yanakudrinskaya.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {

        binding.switchDurkThemes.setOnCheckedChangeListener { _, checked ->
            viewModel.updateTheme(checked)
         }

        binding.settingsShare.setOnClickListener {
            viewModel.getIntent(NavigationEvent.SHARE)
        }

        binding.settingsSupport.setOnClickListener {
            viewModel.getIntent(NavigationEvent.SUPPORT)
        }

        binding.settingsUserAgreement.setOnClickListener {
            viewModel.getIntent(NavigationEvent.AGREEMENT)
        }
    }

    private fun setupObservers() {

        viewModel.getNavigationEvents().observe(viewLifecycleOwner) { event ->
            when (event) {
                is SettingsEvent.Event -> openApp(event)
                is SettingsEvent.Theme -> updateSwitch(event.isDark)
            }
        }
    }

    private fun openApp(event: SettingsEvent.Event) {
        try {
            startActivity(event.intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), event.errorMessage, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun updateSwitch(isDark: Boolean) {
        if (view == null || _binding == null) return
        binding.switchDurkThemes.isChecked = isDark
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}