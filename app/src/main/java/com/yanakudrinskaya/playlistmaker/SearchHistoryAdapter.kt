package com.yanakudrinskaya.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SearchHistoryAdapter () : RecyclerView.Adapter<TrackListViewHolder> (), ItemClickListener {

    var searchHistoryTrackList = listOf<Track>()
    override var onItemClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder = TrackListViewHolder(parent)


    override fun getItemCount(): Int {
        return searchHistoryTrackList.size
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        holder.bind(searchHistoryTrackList[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(searchHistoryTrackList[position])
        }
    }

}