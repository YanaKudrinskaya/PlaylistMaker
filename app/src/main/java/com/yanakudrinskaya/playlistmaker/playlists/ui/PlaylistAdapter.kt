package com.yanakudrinskaya.playlistmaker.playlists.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yanakudrinskaya.playlistmaker.databinding.PlaylistItemBinding
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.search.domain.ItemClickListener
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

class PlaylistAdapter : RecyclerView.Adapter<PlaylistViewHolder> ()
{

    var playlists = mutableListOf<Playlist>()
    var onItemClick: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(PlaylistItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(playlists[position])
        }
    }

    fun removeItems() {
        playlists.clear()
    }

}