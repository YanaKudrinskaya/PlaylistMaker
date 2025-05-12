package com.yanakudrinskaya.playlistmaker

import android.app.Application
import com.yanakudrinskaya.playlistmaker.di.dataModule
import com.yanakudrinskaya.playlistmaker.di.interactorModule
import com.yanakudrinskaya.playlistmaker.di.repositoryModule
import com.yanakudrinskaya.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
    }
}