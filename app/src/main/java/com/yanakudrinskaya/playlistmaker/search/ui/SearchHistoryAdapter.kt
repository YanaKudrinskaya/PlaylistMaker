package com.yanakudrinskaya.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yanakudrinskaya.playlistmaker.databinding.TrackItemBinding
import com.yanakudrinskaya.playlistmaker.search.domain.ItemClickListener
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

class SearchHistoryAdapter : RecyclerView.Adapter<TrackListViewHolder> (), ItemClickListener {

    var searchHistoryTrackList = listOf<Track>()
    override var onItemClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {

        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackListViewHolder(TrackItemBinding.inflate(layoutInspector, parent, false))
    }

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