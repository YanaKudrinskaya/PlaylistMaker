package com.yanakudrinskaya.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yanakudrinskaya.playlistmaker.media.data.db.dao.TrackDao
import com.yanakudrinskaya.playlistmaker.media.data.db.entity.TrackEntity

@Database(version = 2, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao

}