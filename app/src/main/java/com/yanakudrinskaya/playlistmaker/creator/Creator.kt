package com.yanakudrinskaya.playlistmaker.creator

import android.app.Application
import com.yanakudrinskaya.playlistmaker.player.data.PlayerRepositoryImpl
import com.yanakudrinskaya.playlistmaker.player.domain.PlayerInteractor
import com.yanakudrinskaya.playlistmaker.player.domain.PlayerRepository
import com.yanakudrinskaya.playlistmaker.player.domain.TrackPlayer
import com.yanakudrinskaya.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.yanakudrinskaya.playlistmaker.player.domain.impl.TrackPlayerImpl
import com.yanakudrinskaya.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.yanakudrinskaya.playlistmaker.search.data.TracksRepositoryImpl
import com.yanakudrinskaya.playlistmaker.search.data.network.RetrofitClient
import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryRepository
import com.yanakudrinskaya.playlistmaker.search.domain.TracksInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.TracksRepository
import com.yanakudrinskaya.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.yanakudrinskaya.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.yanakudrinskaya.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.yanakudrinskaya.playlistmaker.settings.domain.SettingsInteractor
import com.yanakudrinskaya.playlistmaker.settings.domain.SettingsRepository
import com.yanakudrinskaya.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.yanakudrinskaya.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.yanakudrinskaya.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.yanakudrinskaya.playlistmaker.sharing.domain.ExternalNavigator
import com.yanakudrinskaya.playlistmaker.sharing.domain.SharingInteractor
import com.yanakudrinskaya.playlistmaker.sharing.domain.SharingRepository
import com.yanakudrinskaya.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {

    private lateinit var application: Application

    private val playerRepository = PlayerRepositoryImpl

    fun initApplication(application: Application) {
        Creator.application = application
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitClient(application))
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(application)
    }

    private fun getSettingsRepository() : SettingsRepository {
        return SettingsRepositoryImpl(application)
    }

    private fun getExternalNavigator() : ExternalNavigator {
        return ExternalNavigatorImpl()
    }

    private fun getSharingRepository() : SharingRepository {
        return SharingRepositoryImpl(application)
    }

    private fun getPlayerRepository() : PlayerRepository {
        return playerRepository
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(
            getSearchHistoryRepository()
        )
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(),  getSharingRepository())
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    fun proviedTrackPlayer() : TrackPlayer {
        return TrackPlayerImpl(getPlayerRepository())
    }

}