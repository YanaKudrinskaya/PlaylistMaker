package com.yanakudrinskaya.playlistmaker

import android.app.Application.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson


class SearchHistory (val sharedPreferences: SharedPreferences) {

    fun read(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_LIST_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    fun write(list: List<Track>) {
        sharedPreferences.edit()
            .putString(HISTORY_LIST_KEY, createJsonFromHistoryList(list))
            .commit()
    }

    fun addTrackToHistory(track: Track) {
        val trackList = read().toMutableList()
        val trackListIterator = trackList.iterator()
        while (trackListIterator.hasNext()) {
            if(trackListIterator.next().trackId == track.trackId)
                trackListIterator.remove()
        }
        trackList.add(0, track)
        if(trackList.size > 10) trackList.removeAt(10)
        write(trackList)
    }

    fun clearSearchHistory() {
        write(emptyList<Track>())
    }

    private fun createJsonFromHistoryList(historyList: List<Track>): String {
        return Gson().toJson(historyList)
    }
}