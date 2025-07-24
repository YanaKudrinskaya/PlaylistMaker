package com.yanakudrinskaya.playlistmaker.root.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.root.ui.model.SingleLiveEvent

class RootViewModel : ViewModel() {

    private val navigationEvents = MutableLiveData<Boolean>(true)
    fun getNavigationEvents(): LiveData<Boolean> = navigationEvents

    fun setNavigationVisible(visible: Boolean) {
        navigationEvents.value = visible
    }

}