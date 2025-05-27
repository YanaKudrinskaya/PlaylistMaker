package com.yanakudrinskaya.playlistmaker.media.domain

interface ResourcesProviderRepository {
    fun getString(resId: Int): String
}