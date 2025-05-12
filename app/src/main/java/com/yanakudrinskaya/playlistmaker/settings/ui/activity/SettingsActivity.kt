package com.yanakudrinskaya.playlistmaker.settings.ui.activity

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanakudrinskaya.playlistmaker.databinding.ActivitySettingsBinding
import com.yanakudrinskaya.playlistmaker.settings.ui.model.NavigationEvent
import com.yanakudrinskaya.playlistmaker.settings.ui.model.SettingsEvent
import com.yanakudrinskaya.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModel<SettingsViewModel>()
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupObservers()
        setupClickListeners()
    }

    private fun updateSwitch(isDark: Boolean) {
        binding.switchDurkThemes.isChecked = isDark
    }

    private fun setupClickListeners() {

        binding.toolbarSettings.setNavigationOnClickListener {
            finish()
        }

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

        viewModel.getNavigationEvents().observe(this) { event ->
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
            Toast.makeText(this, event.errorMessage, Toast.LENGTH_LONG)
                .show()
        }
    }

}
