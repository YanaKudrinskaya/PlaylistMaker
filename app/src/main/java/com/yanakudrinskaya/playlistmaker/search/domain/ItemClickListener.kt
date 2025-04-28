package com.yanakudrinskaya.playlistmaker.search.domain

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track


interface ItemClickListener {
    var onItemClick: ((Track) -> Unit)?
}