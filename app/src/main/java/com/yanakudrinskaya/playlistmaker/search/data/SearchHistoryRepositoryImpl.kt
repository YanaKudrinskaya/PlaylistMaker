package com.yanakudrinskaya.playlistmaker.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.settings.data.impl.HISTORY_LIST_KEY


class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
) : SearchHistoryRepository {


    override fun getHistoryList(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_LIST_KEY, null) ?: return emptyArray()
        return gson.fromJson(json, Array<Track>::class.java)
    }

    override fun saveHistoryList(list: List<Track>) {
        sharedPreferences.edit()
            .putString(HISTORY_LIST_KEY, createJsonFromHistoryList(list))
            .apply()
    }


    private fun createJsonFromHistoryList(historyList: List<Track>): String {
        return gson.toJson(historyList)
    }
}