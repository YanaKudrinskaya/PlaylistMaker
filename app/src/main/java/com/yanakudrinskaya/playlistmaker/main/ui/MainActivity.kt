package com.yanakudrinskaya.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanakudrinskaya.playlistmaker.media.ui.MediaActivity
import com.yanakudrinskaya.playlistmaker.databinding.ActivityMainBinding
import com.yanakudrinskaya.playlistmaker.search.ui.activity.SearchActivity
import com.yanakudrinskaya.playlistmaker.settings.ui.activity.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewBinding.searchButton.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

        viewBinding.mediaButton.setOnClickListener {
            val displayIntent = Intent(this, MediaActivity::class.java)
            startActivity(displayIntent)
        }

        viewBinding.settingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
    }
}