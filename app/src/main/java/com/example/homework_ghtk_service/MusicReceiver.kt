package com.example.homework_ghtk_service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.MUSIC_ACTION

class MusicReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action: Int? = intent?.extras?.getInt(MUSIC_ACTION)
        if (context != null && action != null) {
            PlayMusicActivity.startMusicService(context, action, MusicService.songPosition)
        }
    }
}