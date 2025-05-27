package com.yanakudrinskaya.playlistmaker.media.domain.use_cases

import com.yanakudrinskaya.playlistmaker.media.domain.ResourcesProviderRepository

class ResourcesProviderUseCase(
    private val repository: ResourcesProviderRepository
) {
    fun getString(resId: Int): String {
        return repository.getString(resId)
    }
}