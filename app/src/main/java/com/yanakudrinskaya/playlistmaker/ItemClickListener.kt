package com.yanakudrinskaya.playlistmaker

import com.yanakudrinskaya.playlistmaker.domain.models.Track

interface ItemClickListener {
    var onItemClick: ((Track) -> Unit)?
}