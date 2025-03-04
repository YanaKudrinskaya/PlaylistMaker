package com.yanakudrinskaya.playlistmaker

interface ItemClickListener {
    var onItemClick: ((Track) -> Unit)?
}