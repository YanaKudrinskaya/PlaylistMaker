package com.yanakudrinskaya.playlistmaker.create_playlist.domain.model

import android.net.Uri

data class PlaylistCover(
    val filePath: String,
    val uri: Uri? = null,
)
