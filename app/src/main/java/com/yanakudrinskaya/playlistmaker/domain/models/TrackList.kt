package com.yanakudrinskaya.playlistmaker.domain.models

import com.yanakudrinskaya.playlistmaker.data.dto.ResponseStatus

data class TrackList( val list: List<Track>?,
    val status: ResponseStatus)
