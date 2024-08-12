package com.example.homework_ghtk_service

import Song
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.CANNEL_NOTIFICATION
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.CHANGE_LISTENER
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.CHANNEL_ID
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.CHANNEL_NAME
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.MUSIC_ACTION
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.NEXT
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.PAUSE
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.PLAY
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.PREVIOUS
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.RESUME
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.SONG_POSITION

class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    companion object {
        var isPlaying = false
        var listSongPlaying: MutableList<Song> = mutableListOf()
        var songPosition = 0
        var mediaPlayer: MediaPlayer? = null
        var lengthSong = 0
        var action = -1
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.let {
            if (it.containsKey(MUSIC_ACTION)) {
                action = it.getInt(MUSIC_ACTION)
            }
            if (it.containsKey(SONG_POSITION)) {
                songPosition = it.getInt(SONG_POSITION)
            }
            handleActionMusic(action)
        }
        return START_NOT_STICKY
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            PLAY -> playMediaPlayer()
            PREVIOUS -> prevSong()
            NEXT -> nextSong()
            PAUSE -> pauseSong()
            RESUME -> resumeSong()
            CANNEL_NOTIFICATION -> cannelNotification()
            else -> {}
        }
    }


    private fun pauseSong() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
                sendNotificationMedia()
                sendBroadcastChangeListener()
            }
        }
    }

    private fun cannelNotification() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
            }
        }
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifManager.cancelAll()
        sendBroadcastChangeListener()
        stopSelf()
    }

    private fun resumeSong() {
        mediaPlayer?.let {
            it.start()
            isPlaying = true
            sendNotificationMedia()
            sendBroadcastChangeListener()
        }
    }

    private fun prevSong() {
        songPosition = if (listSongPlaying.size > 1) {
            if (songPosition > 0) {
                songPosition - 1
            } else {
                listSongPlaying.size - 1
            }
        } else {
            0
        }
        sendNotificationMedia()
        sendBroadcastChangeListener()
        playMediaPlayer()
    }

    private fun nextSong() {
        if (songPosition < listSongPlaying.size - 1) {
            songPosition++
        } else {
            songPosition = 0
        }
        sendNotificationMedia()
        sendBroadcastChangeListener()
        playMediaPlayer()
    }

    private fun playMediaPlayer() {
        val song = listSongPlaying[songPosition]
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                reset()
                setDataSource(applicationContext, Uri.parse("android.resource://" + packageName + "/" + song.url))
                prepareAsync()
                initControl()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initControl() {
        mediaPlayer?.apply {
            setOnPreparedListener(this@MusicService)
            setOnCompletionListener(this@MusicService)
        }
    }

//    @SuppressLint("RemoteViewLayout", "ResourceType")
//    private fun sendMusicNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_LOW
//            )
//            channel.setSound(null, null)
//            val manager = getSystemService(NotificationManager::class.java)
//            manager.createNotificationChannel(channel)
//        }
//
//        val song = listSongPlaying[songPosition]
//        val pendingFlag =
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        val intent = Intent(this, PlayMusicActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingFlag)
//        val remoteViews = RemoteViews(packageName, R.layout.layout_push_notification_music).apply {
//            setTextViewText(R.id.tv_song_name, song.title)
//            setTextViewText(R.id.tv_artist, song.artist)
//            setOnClickPendingIntent(
//                R.id.img_previous,
//                openMusicReceiver( PREVIOUS)
//            )
//            setOnClickPendingIntent(
//                R.id.img_next,
//                openMusicReceiver( NEXT)
//            )
//            if (isPlaying) {
//                setImageViewResource(R.id.img_play, R.drawable.ic_pause_gray)
//                setOnClickPendingIntent(
//                    R.id.img_play,
//                    openMusicReceiver( PAUSE)
//                )
//            } else {
//                setImageViewResource(R.id.img_play, R.drawable.ic_play_gray)
//                setOnClickPendingIntent(
//                    R.id.img_play,
//                    openMusicReceiver( RESUME)
//                )
//            }
//            setOnClickPendingIntent(
//                R.id.img_close,
//                openMusicReceiver(CANNEL_NOTIFICATION)
//            )
//
//        }
//
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_small_push_notification)
//            .setContentIntent(pendingIntent)
//            .setCustomContentView(remoteViews)
//            .setSound(null)
//            .build()
//        startForeground(1, notification)
//    }

    private fun sendNotificationMedia() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.setSound(null, null)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val song = listSongPlaying[songPosition]
        val bitmap = song.image?.let { BitmapFactory.decodeResource(resources, it) }
//        val uri = Uri.parse("android.resource://" + packageName + "/" + song.url)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this, CHANNEL_ID
        )
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSmallIcon(R.drawable.ic_small_push_notification)
            .setLargeIcon(bitmap)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2,3 /* #1: pause button */)
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)

        if (isPlaying) {
            notificationBuilder.addAction(R.drawable.ic_previous_gray, "Previous", openMusicReceiver(
                PREVIOUS))
                .addAction(
                    R.drawable.ic_pause_gray,
                    "Pause",
                    openMusicReceiver(PAUSE)
                )
                .addAction(R.drawable.ic_next_gray, "Next", openMusicReceiver(NEXT))
                .addAction(R.drawable.ic_close_gray,"close",openMusicReceiver(CANNEL_NOTIFICATION))
        } else {
            notificationBuilder.addAction(R.drawable.ic_previous_gray, "Previous", openMusicReceiver(
                PREVIOUS))
                .addAction(
                    R.drawable.ic_play_gray,
                    "Pause",
                    openMusicReceiver(RESUME)
                )
                .addAction(R.drawable.ic_next_gray, "Next", openMusicReceiver(NEXT))
                .addAction(R.drawable.ic_close_gray,"close",openMusicReceiver(CANNEL_NOTIFICATION))
        }
        val notification = notificationBuilder.build()
        startForeground(1, notification)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        action = NEXT
        nextSong()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        lengthSong = mediaPlayer?.duration ?: 0
        mp?.start()
        isPlaying = true
        action = PLAY
        sendNotificationMedia()
        sendBroadcastChangeListener()
    }

    private fun sendBroadcastChangeListener() {
        val intent = Intent(CHANGE_LISTENER).apply {
            putExtra(MUSIC_ACTION, action)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    fun openMusicReceiver( action: Int): PendingIntent {
        val intent = Intent(this, MusicReceiver::class.java)
        intent.putExtra(MUSIC_ACTION, action)
        val pendingFlag =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getBroadcast(this, action, intent, pendingFlag)
    }

    //    private void sendNotification(Song song) {
    //        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),song.getImage());
    //        Intent intent = new Intent(this,MainActivity.class);
    //        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    //        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.layout_custom_notification);
    //        remoteViews.setTextViewText(R.id.tv_title_song,song.getTitle());
    //        remoteViews.setTextViewText(R.id.tv_single_song,song.getSingle());
    //        remoteViews.setImageViewBitmap(R.id.img_song,bitmap);
    //        remoteViews.setImageViewResource(R.id.img_pause,R.drawable.ic_pause);
    //        if(isPlaying){
    //            remoteViews.setOnClickPendingIntent(R.id.img_pause,getPendingIntent(this,ACTION_PAUSE));
    //            remoteViews.setImageViewResource(R.id.img_pause,R.drawable.ic_pause);
    //        }else {
    //            remoteViews.setOnClickPendingIntent(R.id.img_pause,getPendingIntent(this,ACTION_RESUM));
    //            remoteViews.setImageViewResource(R.id.img_pause,R.drawable.ic_play);
    //        }
    //
    //        remoteViews.setOnClickPendingIntent(R.id.img_cancel,getPendingIntent(this,ACTION_CLEAR));
    //
    //        Notification notification = new NotificationCompat.Builder(this,MyApplication.CHANNEL_ID)
    //                .setSmallIcon(R.drawable.ic_notification)
    //                .setCustomContentView(remoteViews)
    //                .setColor(getResources().getColor(R.color.purple_200))
    //                .setContentIntent(pendingIntent)
    //                .setSound(null)
    //                .build();
    //        startForeground(1,notification);
    //    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }
}