package com.yanakudrinskaya.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackListAdapter () : RecyclerView.Adapter<TrackListViewHolder> (), ItemClickListener {

    var trackList = mutableListOf<Track>()
    override var onItemClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder = TrackListViewHolder(parent)


    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(trackList[position])
        }
    }

    fun removeItems() {
        trackList.clear()
        notifyDataSetChanged()
    }

}
