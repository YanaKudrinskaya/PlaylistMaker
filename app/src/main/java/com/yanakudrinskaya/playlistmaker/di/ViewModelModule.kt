package com.yanakudrinskaya.playlistmaker.di

import com.yanakudrinskaya.playlistmaker.media.ui.view_model.FavoriteViewModel
import com.yanakudrinskaya.playlistmaker.media.ui.view_model.MediaViewModel
import com.yanakudrinskaya.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.yanakudrinskaya.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.yanakudrinskaya.playlistmaker.search.ui.view_model.SearchViewModel
import com.yanakudrinskaya.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        AudioPlayerViewModel(get())
    }

    viewModel {
        SearchViewModel(get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoriteViewModel()
    }

    viewModel {
        PlaylistViewModel()
    }

    viewModel {
        MediaViewModel(get())
    }

}