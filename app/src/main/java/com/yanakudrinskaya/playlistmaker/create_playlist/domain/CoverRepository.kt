package com.yanakudrinskaya.playlistmaker.create_playlist.domain

import android.net.Uri
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import java.io.File

interface CoverRepository {
    suspend fun saveCoverToStorage(uri: Uri): PlaylistCover
    fun getCoverStorageDir(): File
}