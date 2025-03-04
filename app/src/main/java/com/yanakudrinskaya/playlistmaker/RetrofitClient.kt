package com.yanakudrinskaya.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val iTunesBaseUrl = "https://itunes.apple.com"

    fun getClient() : Retrofit =
        Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}