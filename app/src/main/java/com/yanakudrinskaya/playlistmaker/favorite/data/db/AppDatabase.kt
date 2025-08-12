package com.yanakudrinskaya.playlistmaker.favorite.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yanakudrinskaya.playlistmaker.favorite.data.db.dao.TrackDao
import com.yanakudrinskaya.playlistmaker.favorite.data.db.entity.TrackEntity
import com.yanakudrinskaya.playlistmaker.playlists.data.db.dao.PlaylistDao
import com.yanakudrinskaya.playlistmaker.playlists.data.db.dao.PlaylistTrackDao
import com.yanakudrinskaya.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.yanakudrinskaya.playlistmaker.playlists.data.db.entity.PlaylistTrackCrossRef

@Database(
    version = 5,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class
    ]
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}