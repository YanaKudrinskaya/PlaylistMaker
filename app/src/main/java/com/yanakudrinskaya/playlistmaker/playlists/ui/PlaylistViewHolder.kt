package com.yanakudrinskaya.playlistmaker.playlists.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.databinding.PlaylistItemBinding
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.utils.AppUtils
import com.yanakudrinskaya.playlistmaker.utils.formatTrackCount
import java.io.File

class PlaylistViewHolder (private val binding: PlaylistItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist) {
        Glide.with(itemView)
            .load(File(item.cover))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(AppUtils.dpToPx(8f, itemView.context)))
            .into(binding.ivCover)
        binding.tvTitle.text = item.title
        binding.tvTreckCount.text = formatTrackCount(item.tracks.size)
    }
}