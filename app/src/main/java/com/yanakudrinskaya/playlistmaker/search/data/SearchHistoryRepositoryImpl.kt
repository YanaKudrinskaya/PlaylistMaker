package com.yanakudrinskaya.playlistmaker.search.data

import android.content.Context
import com.google.gson.Gson
import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.settings.data.impl.EXAMPLE_PREFERENCES
import com.yanakudrinskaya.playlistmaker.settings.data.impl.HISTORY_LIST_KEY


class SearchHistoryRepositoryImpl (context: Context) : SearchHistoryRepository {

    private val sharedPreferences = context.getSharedPreferences(EXAMPLE_PREFERENCES, Context.MODE_PRIVATE)
    private val gson = Gson()

    override  fun getHistoryList(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_LIST_KEY, null) ?: return emptyArray()
        return gson.fromJson(json, Array<Track>::class.java)
    }

    override fun saveHistoryList(list: List<Track>) {
        sharedPreferences.edit()
            .putString(HISTORY_LIST_KEY, createJsonFromHistoryList(list))
            .commit()
    }


    private fun createJsonFromHistoryList(historyList: List<Track>): String {
        return gson.toJson(historyList)
    }
}