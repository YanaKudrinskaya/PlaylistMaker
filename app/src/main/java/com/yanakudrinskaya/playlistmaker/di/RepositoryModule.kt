package com.yanakudrinskaya.playlistmaker.di

import com.yanakudrinskaya.playlistmaker.create_playlist.data.CoverRepositoryImpl
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.CoverRepository
import com.yanakudrinskaya.playlistmaker.favorite.data.FavoriteRepositoryImpl
import com.yanakudrinskaya.playlistmaker.media.data.ResourcesProviderRepositoryImpl
import com.yanakudrinskaya.playlistmaker.favorite.data.converters.TrackDbConvertor
import com.yanakudrinskaya.playlistmaker.favorite.data.db.AppDatabase
import com.yanakudrinskaya.playlistmaker.media.domain.ResourcesProviderRepository
import com.yanakudrinskaya.playlistmaker.favorite.domain.db.FavoriteRepository
import com.yanakudrinskaya.playlistmaker.player.domain.AudioPlayerRepository
import com.yanakudrinskaya.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.yanakudrinskaya.playlistmaker.playlists.data.PlaylistRepositoryImpl
import com.yanakudrinskaya.playlistmaker.playlists.data.converters.PlaylistConverter
import com.yanakudrinskaya.playlistmaker.playlists.data.db.dao.PlaylistTrackDao
import com.yanakudrinskaya.playlistmaker.playlists.domain.PlaylistRepository
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

    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(
            get<AppDatabase>().playlistDao(),
            get<AppDatabase>().playlistTrackDao(),
            get<AppDatabase>().trackDao(),
            get(),
            get())
    }

    factory<CoverRepository> {
        CoverRepositoryImpl(androidContext())
    }

    factory { TrackDbConvertor() }

    factory { PlaylistConverter() }

}