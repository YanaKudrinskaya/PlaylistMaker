package com.yanakudrinskaya.playlistmaker.data

import android.content.Context
import com.google.gson.Gson
import com.yanakudrinskaya.playlistmaker.domain.api.SearchHistoryRepository
import com.yanakudrinskaya.playlistmaker.domain.models.Track


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