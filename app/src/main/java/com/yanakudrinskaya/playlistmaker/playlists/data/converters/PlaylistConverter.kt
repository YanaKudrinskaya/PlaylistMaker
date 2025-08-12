package com.yanakudrinskaya.playlistmaker.playlists.data.converters

import com.yanakudrinskaya.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

class PlaylistConverter {
    fun map(
        entity: PlaylistEntity,
        tracks: List<Track> = emptyList()
    ): Playlist {
        return Playlist(
            id = entity.id,
            title = entity.title,
            description = entity.description ?: "",
            cover = entity.cover,
            tracks = tracks
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