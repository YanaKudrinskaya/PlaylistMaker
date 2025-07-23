package com.yanakudrinskaya.playlistmaker.create_playlist.domain.impl

import android.net.Uri
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.CoverInteractor
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.CoverRepository
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import java.io.File

class CoverInteractorImpl(
    private val repository: CoverRepository
):  CoverInteractor {
    override suspend fun saveCoverToStorage(uri: Uri): PlaylistCover {
        return repository.saveCoverToStorage(uri)
    }

    override fun getCoverStorageDir(): File {
        return repository.getCoverStorageDir()
    }

}