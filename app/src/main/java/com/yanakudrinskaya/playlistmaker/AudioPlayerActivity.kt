package com.yanakudrinskaya.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var  track: Track

    private lateinit var toolbar: Toolbar
    private lateinit var trackImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var buttonPlay: ImageButton
    private lateinit var buttonCopy: ImageButton
    private lateinit var buttonLike: ImageButton
    private lateinit var duration: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var collectionNameGroup: Group


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audio_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.toolbar_audio_player)
        trackImage = findViewById(R.id.track_image)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        buttonPlay = findViewById(R.id.button_play)
        buttonCopy = findViewById(R.id.button_copy)
        buttonLike = findViewById(R.id.button_like)
        duration = findViewById(R.id.duration_value)
        collectionName = findViewById(R.id.collection_name_value)
        releaseDate = findViewById(R.id.release_date_value)
        primaryGenreName = findViewById(R.id.primary_genre_name_value)
        country = findViewById(R.id.country_value)
        collectionNameGroup = findViewById(R.id.collection_name_group)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        track = intent.getSerializableExtra("track") as Track
        Log.d("Player", "${track.trackName} ")


        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(AppUtils.dpToPx(8f, this)))
            .into(trackImage)

        collectionNameGroup.visibility = if(track.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE

        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        collectionName.text = track.collectionName
        releaseDate.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
    }
}