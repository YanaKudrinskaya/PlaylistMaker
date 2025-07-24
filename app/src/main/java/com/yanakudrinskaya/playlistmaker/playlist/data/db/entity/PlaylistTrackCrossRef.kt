package com.yanakudrinskaya.playlistmaker.playlist.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    val trackId: Int,
    val position: Int
)