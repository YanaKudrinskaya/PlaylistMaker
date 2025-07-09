package com.yanakudrinskaya.playlistmaker.media.data

import android.util.Log
import com.yanakudrinskaya.playlistmaker.media.data.converters.TrackDbConvertor
import com.yanakudrinskaya.playlistmaker.media.data.db.AppDatabase
import com.yanakudrinskaya.playlistmaker.media.data.db.entity.TrackEntity
import com.yanakudrinskaya.playlistmaker.media.domain.db.FavoriteRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteRepository {

    override suspend  fun addTrackToFavorite(track: Track) {
        Log.d("MyLog", "Добавлен трек ${track.trackId}")
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

     override suspend fun deleteTrackFromFavorite(trackId: Int) {
         Log.d("MyLog", "Удален трек $trackId")
        appDatabase.trackDao().deleteTrack(trackId)
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return appDatabase.trackDao().getIdsTracks()
    }

    override fun getFavoriteList(): Flow<List<Track>> {
        return  appDatabase.trackDao().getTracks()
            .map { tracks -> convertFromTrackEntity(tracks) }
    }



    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}