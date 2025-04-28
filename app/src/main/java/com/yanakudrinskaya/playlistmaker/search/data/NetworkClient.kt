package com.yanakudrinskaya.playlistmaker.search.data

import com.yanakudrinskaya.playlistmaker.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}