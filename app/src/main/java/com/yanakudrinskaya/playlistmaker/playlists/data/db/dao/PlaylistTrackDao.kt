package com.yanakudrinskaya.playlistmaker.playlists.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yanakudrinskaya.playlistmaker.playlists.data.db.entity.PlaylistTrackCrossRef

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY position ASC")
    suspend fun getTracksForPlaylist(playlistId: Long): List<PlaylistTrackCrossRef>

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteByPlaylistIdAndTrackId(playlistId: Long, trackId: Int)
}