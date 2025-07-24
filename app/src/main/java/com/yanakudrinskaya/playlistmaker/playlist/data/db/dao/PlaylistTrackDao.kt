package com.yanakudrinskaya.playlistmaker.playlist.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yanakudrinskaya.playlistmaker.playlist.data.db.entity.PlaylistTrackCrossRef

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun deleteByPlaylistId(playlistId: Long)

    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getTracksForPlaylist(playlistId: Long): List<PlaylistTrackCrossRef>
}