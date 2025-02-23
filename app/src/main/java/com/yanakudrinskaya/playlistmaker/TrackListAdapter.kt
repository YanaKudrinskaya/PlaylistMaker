package com.yanakudrinskaya.playlistmaker

import android.app.Application.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackListAdapter () : RecyclerView.Adapter<TrackListViewHolder> () {

    var trackList = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder = TrackListViewHolder(parent)


    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            val sharedPrefs = holder.itemView.context.getSharedPreferences(EXAMPLE_PREFERENCES, MODE_PRIVATE)
            val searchHistory = SearchHistory(sharedPrefs)
            searchHistory.addTrackToHistory(trackList[position])
        }
    }

    fun removeItems() {
        trackList.clear()
        notifyDataSetChanged()
    }

}
