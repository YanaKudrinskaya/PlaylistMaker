package com.yanakudrinskaya.playlistmaker.root.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanakudrinskaya.playlistmaker.R

class RootViewModel : ViewModel() {

    private val navigationEvents = MutableLiveData<Boolean>()
    fun getNavigationEvents(): LiveData<Boolean> = navigationEvents

    fun changeDestination(destination: Int) {
        navigationEvents.value = if(destination == R.id.audioPlayerFragment) true else false
    }

}