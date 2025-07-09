package com.yanakudrinskaya.playlistmaker.media.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "track_table",
    indices = [Index(value = ["trackId"], unique = true)]
)

data class TrackEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?,
)