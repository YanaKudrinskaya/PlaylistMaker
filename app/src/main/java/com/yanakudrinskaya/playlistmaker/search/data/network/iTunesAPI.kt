package com.yanakudrinskaya.playlistmaker.search.data.network

import com.yanakudrinskaya.playlistmaker.search.data.dto.TracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {

    @GET("/search?entity=song")
    suspend fun getTrackList(@Query("term") text: String): TracksResponse
}