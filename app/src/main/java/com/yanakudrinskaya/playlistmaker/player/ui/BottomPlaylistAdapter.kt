package com.yanakudrinskaya.playlistmaker.player.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yanakudrinskaya.playlistmaker.databinding.BottomSheetPlaylistItemBinding
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist

class BottomPlaylistAdapter : RecyclerView.Adapter<BottomPlaylistViewHolder> () {

    var playlists = mutableListOf<Playlist>()
    var onItemClick: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomPlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return BottomPlaylistViewHolder(BottomSheetPlaylistItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: BottomPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(playlists[position])
        }
    }

    fun removeItems() {
        playlists.clear()
    }
}