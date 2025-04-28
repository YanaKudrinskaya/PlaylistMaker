package com.yanakudrinskaya.playlistmaker.search.data.mapper

import com.yanakudrinskaya.playlistmaker.search.data.dto.TrackDto
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track


object DtoToTrackMapper {
    fun map (dto: TrackDto) : Track {
        return Track(
            dto.trackId,
            dto.trackName,
            dto.artistName,
            dto.trackTimeMillis,
            dto.artworkUrl100,
            dto.collectionName,
            dto.releaseDate,
            dto.primaryGenreName,
            dto.country,
            dto.previewUrl
        )
    }
}