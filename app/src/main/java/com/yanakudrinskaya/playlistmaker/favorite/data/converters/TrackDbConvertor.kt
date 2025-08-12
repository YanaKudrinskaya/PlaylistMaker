package com.yanakudrinskaya.playlistmaker.favorite.data.converters

import com.yanakudrinskaya.playlistmaker.favorite.data.db.entity.TrackEntity
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

class TrackDbConvertor {

    fun map(track: Track, isFavorite: Boolean = false): TrackEntity {
        return TrackEntity(
            id = 0,
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate ?: "",
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = isFavorite
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite
        )
    }
}