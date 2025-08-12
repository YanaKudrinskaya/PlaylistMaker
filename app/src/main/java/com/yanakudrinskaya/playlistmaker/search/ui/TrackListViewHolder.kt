package com.yanakudrinskaya.playlistmaker.search.ui

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.yanakudrinskaya.playlistmaker.utils.AppUtils
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.databinding.TrackItemBinding
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale


class TrackListViewHolder(private val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Track) {
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(AppUtils.dpToPx(2f, itemView.context)))
            .into(binding.trackImage)
        binding.trackName.text = item.trackName
        binding.artistName.text = item.artistName
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis.toLong())
        binding.artistName.requestLayout()
    }
}