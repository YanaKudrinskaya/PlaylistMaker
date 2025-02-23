package com.yanakudrinskaya.playlistmaker

import android.app.Application.MODE_PRIVATE
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SearchHistoryAdapter () : RecyclerView.Adapter<TrackListViewHolder> () {

    var searchHistoryTrackList = listOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder = TrackListViewHolder(parent)


    override fun getItemCount(): Int {
        return searchHistoryTrackList.size
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        holder.bind(searchHistoryTrackList[position])
    }

}