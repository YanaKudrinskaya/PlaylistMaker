package com.yanakudrinskaya.playlistmaker.data.network

import com.yanakudrinskaya.playlistmaker.data.NetworkClient
import com.yanakudrinskaya.playlistmaker.data.dto.Response
import com.yanakudrinskaya.playlistmaker.data.dto.ResponseStatus
import com.yanakudrinskaya.playlistmaker.data.dto.TracksResponse
import com.yanakudrinskaya.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitClient : NetworkClient {

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is TracksSearchRequest) {
                val resp = iTunesService.getTrackList(dto.expression).execute()
                val body = resp.body() ?: Response().apply {
                    status = ResponseStatus.SERVER_ERROR
                }

                body.apply {
                    status = when (resp.code()) {
                        200 -> ResponseStatus.SUCCESS
                        400 -> ResponseStatus.BAD_REQUEST
                        500 -> ResponseStatus.SERVER_ERROR
                        else -> ResponseStatus.UNKNOWN_ERROR
                    }
                }
            } else {
                Response().apply { status = ResponseStatus.BAD_REQUEST }
            }
        } catch (e: IOException) {
            Response().apply {
                status = ResponseStatus.NO_INTERNET
            }
        } catch (e: Exception) {
            Response().apply {
                status = ResponseStatus.UNKNOWN_ERROR
            }
        }
    }
}
