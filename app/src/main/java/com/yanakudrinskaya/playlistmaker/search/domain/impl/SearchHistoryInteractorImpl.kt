package com.yanakudrinskaya.playlistmaker.search.domain.impl

import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl (private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun getHistoryList(consumer: SearchHistoryInteractor.SearchHistoryConsumer) /*: MutableList<Track>*/ {
        val history =  repository.getHistoryList().toMutableList()
        consumer.consume(history)
    }

    override fun saveHistoryList(list: List<Track>) {
        repository.saveHistoryList(list)
    }

    override fun addTrackToHistory(track: Track) {
        val trackList = repository.getHistoryList().toMutableList()
        val trackListIterator = trackList.iterator()
        while (trackListIterator.hasNext()) {
            if(trackListIterator.next().trackId == track.trackId)
                trackListIterator.remove()
        }
        trackList.add(0, track)
        if(trackList.size > 10) trackList.removeAt(10)
        saveHistoryList(trackList)
    }

    override fun clearSearchHistory() {
        saveHistoryList(emptyList<Track>())
    }

}