package com.yanakudrinskaya.playlistmaker.playlist.data.converters

import com.yanakudrinskaya.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.yanakudrinskaya.playlistmaker.playlist.domain.models.Playlist

class PlaylistConverter {
    fun map(
        entity: PlaylistEntity,
        trackIds: List<Int> = emptyList()
    ): Playlist {
        return Playlist(
            id = entity.id,
            title = entity.title,
            description = entity.description?: "",
            cover = entity.cover,
            trackIds = trackIds
        )
    }

    fun map(domain: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            cover = domain.cover
        )
    }
}