package com.yanakudrinskaya.playlistmaker.di

import com.yanakudrinskaya.playlistmaker.create_playlist.ui.view_model.CreatePlaylistViewModel
import com.yanakudrinskaya.playlistmaker.favorite.ui.view_model.FavoriteViewModel
import com.yanakudrinskaya.playlistmaker.media.ui.view_model.MediaViewModel
import com.yanakudrinskaya.playlistmaker.playlist.ui.view_model.PlaylistViewModel
import com.yanakudrinskaya.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.yanakudrinskaya.playlistmaker.search.ui.view_model.SearchViewModel
import com.yanakudrinskaya.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.yanakudrinskaya.playlistmaker.root.ui.view_model.RootViewModel
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (track: Track) ->
        AudioPlayerViewModel(track, get(), get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoriteViewModel(get())
    }

    viewModel {
        PlaylistViewModel(get())
    }

    viewModel {
        MediaViewModel(get())
    }

    viewModel {
        RootViewModel()
    }

    viewModel {
        CreatePlaylistViewModel(get(), get(), get())
    }

}