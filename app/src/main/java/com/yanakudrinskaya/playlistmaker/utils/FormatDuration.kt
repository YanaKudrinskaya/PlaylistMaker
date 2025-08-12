package com.yanakudrinskaya.playlistmaker.utils

fun formatDuration(millis: Long): String {
    val totalMinutes = millis / 1000 / 60

    return when {
        totalMinutes % 100 in 11..14 -> "$totalMinutes минут"
        totalMinutes % 10 == 1L -> "$totalMinutes минута"
        totalMinutes % 10 in 2..4 -> "$totalMinutes минуты"
        else -> "$totalMinutes минут"
    }
}