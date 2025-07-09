package com.yanakudrinskaya.playlistmaker.di

import com.yanakudrinskaya.playlistmaker.media.data.FavoriteRepositoryImpl
import com.yanakudrinskaya.playlistmaker.media.data.ResourcesProviderRepositoryImpl
import com.yanakudrinskaya.playlistmaker.media.data.converters.TrackDbConvertor
import com.yanakudrinskaya.playlistmaker.media.data.db.AppDatabase
import com.yanakudrinskaya.playlistmaker.media.domain.ResourcesProviderRepository
import com.yanakudrinskaya.playlistmaker.media.domain.db.FavoriteRepository
import com.yanakudrinskaya.playlistmaker.player.domain.AudioPlayerRepository
import com.yanakudrinskaya.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.yanakudrinskaya.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.yanakudrinskaya.playlistmaker.search.data.TracksRepositoryImpl
import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryRepository
import com.yanakudrinskaya.playlistmaker.search.domain.TracksRepository
import com.yanakudrinskaya.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.yanakudrinskaya.playlistmaker.settings.domain.SettingsRepository
import com.yanakudrinskaya.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.yanakudrinskaya.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.yanakudrinskaya.playlistmaker.sharing.domain.ExternalNavigator
import com.yanakudrinskaya.playlistmaker.sharing.domain.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {


    factory<TracksRepository> {
        TracksRepositoryImpl(get(), androidContext())
    }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get(), androidContext())
    }

    factory<SharingRepository> {
        SharingRepositoryImpl(androidContext())
    }

    factory<ExternalNavigator> {
        ExternalNavigatorImpl()
    }

    single <ResourcesProviderRepository> {
        ResourcesProviderRepositoryImpl(androidContext())
    }

    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl()
    }

    factory<FavoriteRepository> {
        FavoriteRepositoryImpl(get<AppDatabase>().trackDao(), get())
    }

    factory { TrackDbConvertor() }

}