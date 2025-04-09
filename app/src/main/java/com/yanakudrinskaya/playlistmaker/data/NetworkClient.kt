package com.yanakudrinskaya.playlistmaker.data

import com.yanakudrinskaya.playlistmaker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}