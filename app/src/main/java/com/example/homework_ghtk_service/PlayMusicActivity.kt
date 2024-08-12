package com.example.homework_ghtk_service

import Song
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.homework_ghtk_service.databinding.ActivityMainBinding
import java.util.Timer
import java.util.TimerTask


class PlayMusicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mTimer: Timer? = null
    private var mAction = 0
    companion object{
        const val PLAY: Int = 0
        const val PAUSE: Int = 1
        const val NEXT: Int = 2
        const val PREVIOUS: Int = 3
        const val RESUME: Int = 4
        const val CANNEL_NOTIFICATION: Int = 5
        const val MUSIC_ACTION: String = "musicAction"
        const val SONG_POSITION: String = "songPosition"
        const val CHANGE_LISTENER: String = "change_listener"
        const val CHANNEL_ID: String = "channel_music_basic_id"
        const val CHANNEL_NAME: String = "channel_music_basic_name"
        fun startMusicService(context: Context, action: Int, songPosition: Int) {
            val musicService = Intent(context, MusicService::class.java)
            musicService.putExtra(MUSIC_ACTION, action)
            musicService.putExtra(SONG_POSITION, songPosition)
            context.startService(musicService)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(musicService)
            } else {
                context.startService(musicService)
            }
        }
    }
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mAction = intent.getIntExtra(MUSIC_ACTION, 0)
            handleMusicAction()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, IntentFilter(CHANGE_LISTENER))
        initControl()
        showInforSong()
        setUpVolume()
        mAction = MusicService.action
        handleMusicAction()
    }

    private fun initControl() {
        mTimer = Timer()

        binding.imgPrevious.setOnClickListener{ clickOnPrevButton()}
        binding.imgPlay.setOnClickListener{clickOnPlayButton()}
        binding.imgNext.setOnClickListener{clickOnNextButton()}

        binding.seekbarTimeLine.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                MusicService.mediaPlayer!!.seekTo(seekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            }
        })
    }

    private fun handleMusicAction() {
        if (CANNEL_NOTIFICATION == mAction) {
            return
        }
        when (mAction) {
            PREVIOUS,
            NEXT -> {
                showInforSong()
            }

            PLAY -> {
                showInforSong()
                showSeekBar()
                showStatusButtonPlay()
            }

            PAUSE -> {
                showSeekBar()
                showStatusButtonPlay()
            }

            RESUME -> {
                showSeekBar()
                showStatusButtonPlay()
            }
        }
    }


    private fun showInforSong() {
        if (MusicService.listSongPlaying.isEmpty()) {
            return
        }
        val currentSong: Song = MusicService.listSongPlaying[MusicService.songPosition]
        binding.tvSongName.text= currentSong.title
        binding.tvArtist.text = currentSong.artist
        binding.imgSong.setImageResource(currentSong.image ?: R.drawable.image_no_available)
    }

    private fun showSeekBar() {
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    binding.tvTimeCurrent.text =
                        (getTime(MusicService.mediaPlayer!!.currentPosition))
                    binding.tvTimeMax.text = (getTime(MusicService.lengthSong))
                    binding.seekbarTimeLine.max = (MusicService.lengthSong)
                    binding.seekbarTimeLine.progress = (MusicService.mediaPlayer!!.currentPosition)
                }
            }
        }, 0, 1000)
    }

    fun getTime(millis: Int): String {
        val second = ((millis / 1000) % 60).toLong()
        val minute = (millis / (1000 * 60)).toLong()
        return String.format("%02d:%02d", minute, second)
    }

    private fun showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            binding.imgPlay.setImageResource(R.drawable.ic_pause_black)
        } else {
            binding.imgPlay.setImageResource(R.drawable.ic_play_black)
        }
    }

    private fun clickOnPrevButton() {
        startMusicService(this, PREVIOUS, MusicService.songPosition
        )
    }

    private fun clickOnNextButton() {
        startMusicService(this, NEXT, MusicService.songPosition)
    }

    private fun clickOnPlayButton() {
        if(MusicService.songPosition<0){
//            MusicService.clearListSongPlaying()
//            MusicService.mListSongPlaying.addAll(initListSong())
            Log.d("test","${MusicService.listSongPlaying.size}" )
            MusicService.isPlaying = false
            startMusicService(this, PLAY, 0)
        }
        if (MusicService.isPlaying) {
            startMusicService(this, PAUSE, MusicService.songPosition)
        } else {
            startMusicService(this, RESUME, MusicService.songPosition)
        }
    }

    private fun setUpVolume(){
        binding.imgVolumn.setOnClickListener{
            if(binding.seekbarVolume.visibility== View.VISIBLE){
                binding.seekbarVolume.visibility= View.GONE
            }else{
                binding.seekbarVolume.visibility= View.VISIBLE
            }
        }
        val audioManager : AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        // Cài đặt giá trị hiện tại cho SeekBar
        binding.seekbarVolume.progress = currentVolume
        binding.seekbarVolume.max = maxVolume

        // Lắng nghe thay đổi của SeekBar
        binding.seekbarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Cập nhật âm lượng khi người dùng thay đổi SeekBar
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Có thể thêm mã xử lý khi người dùng bắt đầu kéo SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Có thể thêm mã xử lý khi người dùng ngừng kéo SeekBar
            }
        })
    }


}