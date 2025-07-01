package com.yanakudrinskaya.playlistmaker.search.data

import com.yanakudrinskaya.playlistmaker.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response

}