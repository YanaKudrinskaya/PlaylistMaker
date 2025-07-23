package com.yanakudrinskaya.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.yanakudrinskaya.playlistmaker.search.data.NetworkClient
import com.yanakudrinskaya.playlistmaker.search.data.network.RetrofitClient
import com.yanakudrinskaya.playlistmaker.search.data.network.iTunesApi
import com.yanakudrinskaya.playlistmaker.favorite.data.db.AppDatabase
import com.yanakudrinskaya.playlistmaker.settings.data.impl.EXAMPLE_PREFERENCES
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<iTunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(EXAMPLE_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitClient(get(), androidContext())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

}