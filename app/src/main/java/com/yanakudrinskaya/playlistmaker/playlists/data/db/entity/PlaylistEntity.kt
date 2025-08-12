package com.yanakudrinskaya.playlistmaker.playlists.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_table"
)

data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String? = null,
    val cover: String,
)