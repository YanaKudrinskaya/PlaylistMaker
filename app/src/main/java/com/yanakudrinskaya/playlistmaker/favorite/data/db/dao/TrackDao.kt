package com.yanakudrinskaya.playlistmaker.favorite.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yanakudrinskaya.playlistmaker.favorite.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM track_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int)

    @Query("SELECT * FROM track_table ORDER BY id DESC")
    fun getTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table")
    suspend fun getIdsTracks(): List<Int>

    @Query("SELECT * FROM track_table WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Int>): List<TrackEntity>

    @Query("UPDATE track_table SET isFavorite = :isFavorite WHERE trackId = :trackId")
    suspend fun updateFavoriteStatus(trackId: Int, isFavorite: Boolean)
}