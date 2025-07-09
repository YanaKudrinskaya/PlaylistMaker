package com.yanakudrinskaya.playlistmaker.di

import com.yanakudrinskaya.playlistmaker.media.domain.db.FavoriteInteractor
import com.yanakudrinskaya.playlistmaker.media.domain.impl.FavoriteInteractorImpl
import com.yanakudrinskaya.playlistmaker.media.domain.use_cases.ResourcesProviderUseCase
import com.yanakudrinskaya.playlistmaker.player.domain.TrackPlayerInteractor
import com.yanakudrinskaya.playlistmaker.player.domain.impl.TrackPlayerInteractorImpl
import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.TracksInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.yanakudrinskaya.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.yanakudrinskaya.playlistmaker.settings.domain.SettingsInteractor
import com.yanakudrinskaya.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.yanakudrinskaya.playlistmaker.sharing.domain.SharingInteractor
import com.yanakudrinskaya.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get(), get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    single <ResourcesProviderUseCase> {
        ResourcesProviderUseCase(get())
    }

    factory<TrackPlayerInteractor> {
        TrackPlayerInteractorImpl(get())
    }

    factory<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }

}