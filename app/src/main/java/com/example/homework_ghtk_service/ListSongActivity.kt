package com.example.homework_ghtk_service

import Song
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.PLAY
import com.example.homework_ghtk_service.PlayMusicActivity.Companion.startMusicService
import com.example.homework_ghtk_service.databinding.ActivityMain2Binding

class ListSongActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rcvData.setLayoutManager(linearLayoutManager)
        // Kiểm tra và yêu cầu quyền thông báo
        if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(POST_NOTIFICATIONS),
                99)
        }

        val songAdapter = SongAdapter(initListSong()) { position ->
            MusicService.listSongPlaying.addAll(initListSong())
            MusicService.isPlaying = false
            startMusicService(this,PLAY, position )
            val intent = Intent(this,PlayMusicActivity::class.java)
            startActivity(intent)

        }
        binding.rcvData.adapter = songAdapter
    }

    private fun  initListSong() : MutableList<Song>{
        val listSong : MutableList<Song> = mutableListOf()
        listSong.add(Song("Thà rằng như thế",R.drawable.tharangnhuthe,R.raw.tharangnhuthe,"Ưng Hoàng Phúc"))
        listSong.add(Song("Tình khúc vàng",R.drawable.tinhkhucvang,R.raw.tinhkhucvang,"Đan Trường"))
        listSong.add(Song("Đừng buông tay anh",R.drawable.dungbuongtayanh,R.raw.dungbuongtayanh,"Hồ Quang Hiếu"))
        listSong.add(Song("Không cảm xúc",R.drawable.khongcamxuc,R.raw.khongcamxuc,"hồ Quang Hếu"))
        listSong.add(Song("Nụ hôn và nước mắt ",R.drawable.nuhonvanuocmat,R.raw.nuhonvanuocmat,"Lâm Chấn Huy"))
        listSong.add(Song("Em dối tôi để làm gì",R.drawable.emdoitoidelamgi,R.raw.emdoitoidelamgi,"Lâm Chấn Huy"))
        listSong.add(Song("Anh đi rồi anh sẽ về",R.drawable.anhdiroianhseve,R.raw.anhdiroianhseve,"Lâm Chấn Huy"))
        listSong.add(Song("Kiếp ve sầu",R.drawable.kiepvesau,R.raw.kiepvesau,"Đan Trường"))

        return listSong
    }
}