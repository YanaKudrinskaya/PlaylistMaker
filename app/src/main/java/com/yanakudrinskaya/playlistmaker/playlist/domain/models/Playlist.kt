package com.yanakudrinskaya.playlistmaker.playlist.domain.models

data class Playlist(
    val id: Long,
    val title: String,
    val description: String = "",
    val cover: String = "",
    val trackIds: List<Int>
)
