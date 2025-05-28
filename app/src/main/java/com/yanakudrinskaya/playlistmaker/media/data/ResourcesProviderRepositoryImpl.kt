package com.yanakudrinskaya.playlistmaker.media.data

import android.content.Context
import com.yanakudrinskaya.playlistmaker.media.domain.ResourcesProviderRepository

class ResourcesProviderRepositoryImpl(
    private val context: Context
) : ResourcesProviderRepository{
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}