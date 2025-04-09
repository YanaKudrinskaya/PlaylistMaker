package com.yanakudrinskaya.playlistmaker

import android.app.Application.MODE_PRIVATE
import android.content.Context
import com.yanakudrinskaya.playlistmaker.data.SearchHistoryRepositoryImpl
import com.yanakudrinskaya.playlistmaker.data.SettingsRepositoryImpl
import com.yanakudrinskaya.playlistmaker.data.TracksRepositoryImpl
import com.yanakudrinskaya.playlistmaker.data.network.RetrofitClient
import com.yanakudrinskaya.playlistmaker.domain.api.SearchHistoryInteractor
import com.yanakudrinskaya.playlistmaker.domain.api.SearchHistoryRepository
import com.yanakudrinskaya.playlistmaker.domain.api.SettingsInteractor
import com.yanakudrinskaya.playlistmaker.domain.api.SettingsRepository
import com.yanakudrinskaya.playlistmaker.domain.api.TracksRepository
import com.yanakudrinskaya.playlistmaker.domain.api.TracksInteractor
import com.yanakudrinskaya.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.yanakudrinskaya.playlistmaker.domain.impl.SettingsInteractorImpl
import com.yanakudrinskaya.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitClient())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context)
    }

    private fun getSettingsRepository(context: Context) :SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

}