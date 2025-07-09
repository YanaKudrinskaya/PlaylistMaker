package com.yanakudrinskaya.playlistmaker.media.data.converters

import com.yanakudrinskaya.playlistmaker.media.data.db.entity.TrackEntity
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            id = 0,
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate?: "",
            track.primaryGenreName,
            track.country,
            track.previewUrl,
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            isFavorite = true
        )
    }
}