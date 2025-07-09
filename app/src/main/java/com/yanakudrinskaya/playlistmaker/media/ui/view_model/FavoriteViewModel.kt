package com.yanakudrinskaya.playlistmaker.media.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.playlistmaker.media.domain.db.FavoriteInteractor
import com.yanakudrinskaya.playlistmaker.media.ui.model.FavoriteScreenState
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private var favoriteJob: Job? = null

    private val favoriteStateLiveData = MutableLiveData<FavoriteScreenState>()
    fun getFavoriteStateLiveData(): LiveData<FavoriteScreenState> = favoriteStateLiveData

    init {
         getFavoriteList()
    }

    private fun getFavoriteList() {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favoriteInteractor
                .getFavoriteList()
                .collect { list ->
                    processResult(list)
                }
        }
    }

    private fun processResult(list: List<Track>) {
        if (list.isEmpty()) {
            favoriteStateLiveData.postValue(FavoriteScreenState.Empty)
        } else {
            favoriteStateLiveData.postValue(FavoriteScreenState.Content(list))
        }
    }
}