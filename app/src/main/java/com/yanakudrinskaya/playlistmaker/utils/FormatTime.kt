package com.yanakudrinskaya.playlistmaker.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatTime(milliseconds: Int): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(milliseconds)
}