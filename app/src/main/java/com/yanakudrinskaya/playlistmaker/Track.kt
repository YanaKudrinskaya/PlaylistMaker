package com.yanakudrinskaya.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

data class Track (
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String
)

