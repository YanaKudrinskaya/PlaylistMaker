package com.yanakudrinskaya.playlistmaker.playlists.domain.models

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import java.io.Serializable

data class Playlist(
    val id: Long,
    val title: String,
    val description: String = "",
    val cover: String = "",
    val tracks: List<Track>
) : Serializable
