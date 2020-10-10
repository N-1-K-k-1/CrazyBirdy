package com.n1Kk1.crazybirdy

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var volImg: ImageView
    private lateinit var music: MediaPlayer
    private var isMute = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Paper.init(this)
        volImg = findViewById(R.id.volume)

        music = MediaPlayer.create(baseContext, R.raw.synthwave_loop_track_2)
        music.isLooping = true

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        play.setOnClickListener {
            startActivity(Intent(this, MainGameActivity::class.java))
        }

        Paper.book().write("isMute", isMute)

        volume.setOnClickListener {
            isMute = !isMute
            if (isMute) {
                volume.setImageResource(R.drawable.ic_baseline_volume_off_24)
                music.setVolume(0f, 0f)
            }
            else {
                volume.setImageResource(R.drawable.ic_baseline_volume_up_24)
                music.setVolume(1f, 1f)
            }

            Paper.book().write("isMute", isMute)
        }
    }

    override fun onResume() {
        super.onResume()

        if (!isMute)
            music.start()

        highScore.text = getString(R.string.highScore, Paper.book().read("highScore", 0))

    }

    override fun onPause() {
        super.onPause()

        if (!isMute && music.isPlaying)
            music.stop()
    }
}