package com.yanakudrinskaya.playlistmaker.create_playlist.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.CoverRepository
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import java.io.File
import java.io.FileOutputStream

class CoverRepositoryImpl(
    private val context: Context
): CoverRepository {

    override suspend fun saveCoverToStorage(uri: Uri): PlaylistCover {
        val file = getCoverFile()
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return PlaylistCover( file.absolutePath, uri)
    }

    override fun getCoverStorageDir(): File {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")
            .apply { mkdirs() }
    }

    private fun getCoverFile(): File {
        return File(getCoverStorageDir(), "cover_${System.currentTimeMillis()}.jpg")
    }
}