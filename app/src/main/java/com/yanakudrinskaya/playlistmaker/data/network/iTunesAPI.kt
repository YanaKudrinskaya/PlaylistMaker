package com.yanakudrinskaya.playlistmaker.data.network

import com.yanakudrinskaya.playlistmaker.data.dto.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {

    @GET("/search?entity=song")
    fun getTrackList(@Query("term") text: String): Call<TracksResponse>
}