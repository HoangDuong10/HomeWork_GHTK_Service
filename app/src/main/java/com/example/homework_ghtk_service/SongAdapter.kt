package com.example.homework_ghtk_service

import Song
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_ghtk_service.databinding.ItemSongBinding

class SongAdapter(
    private val mListSongs: List<Song>,
    private val onClickItemSong: (Int) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemSongBinding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(itemSongBinding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = mListSongs.getOrNull(position) ?: return
        song.image?.let { holder.itemSongBinding.imgSong.setImageResource(it) }
        holder.itemSongBinding.tvSongName.text = song.title
        holder.itemSongBinding.tvArtist.text = song.artist

        holder.itemSongBinding.layoutItem.setOnClickListener {
            onClickItemSong.invoke(position)
        }
    }

    override fun getItemCount(): Int = mListSongs.size

    class SongViewHolder(val itemSongBinding: ItemSongBinding) : RecyclerView.ViewHolder(itemSongBinding.root)
}