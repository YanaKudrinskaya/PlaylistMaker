package com.yanakudrinskaya.playlistmaker.presentation.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yanakudrinskaya.playlistmaker.ItemClickListener
import com.yanakudrinskaya.playlistmaker.domain.models.Track

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