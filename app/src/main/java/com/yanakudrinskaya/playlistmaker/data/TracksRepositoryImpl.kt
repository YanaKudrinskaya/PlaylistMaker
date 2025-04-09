package com.yanakudrinskaya.playlistmaker.data

import android.util.Log
import com.yanakudrinskaya.playlistmaker.data.dto.ResponseStatus
import com.yanakudrinskaya.playlistmaker.data.dto.TracksResponse
import com.yanakudrinskaya.playlistmaker.data.dto.TracksSearchRequest
import com.yanakudrinskaya.playlistmaker.domain.api.TracksRepository
import com.yanakudrinskaya.playlistmaker.domain.models.Track
import com.yanakudrinskaya.playlistmaker.domain.models.TrackList

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): TrackList {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.status) {
            ResponseStatus.SUCCESS -> {
                val tracks = (response as TracksResponse).results.map { dto ->
                    Track(
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
                return TrackList(tracks, response.status)
            }
            else -> return TrackList(null, response.status)


        }
    }
}
