package com.yanakudrinskaya.playlistmaker.root.ui.view_model

import androidx.lifecycle.ViewModel
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.root.ui.model.SingleLiveEvent

class RootViewModel : ViewModel() {

    private val navigationEvents = SingleLiveEvent<Boolean>()
    fun getNavigationEvents(): SingleLiveEvent<Boolean> = navigationEvents

    fun changeDestination(destination: Int) {
        navigationEvents.value = (destination == R.id.audioPlayerFragment)
    }

}