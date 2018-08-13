package com.demo.android.flb.demo.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.demo.android.flb.demo.base.PlayerObservable
import com.demo.android.flb.demo.utils.DateUtils
import com.demo.android.flb.demo.utils.KeyValueUtils
import com.demo.android.flb.kotlin_upnp_demo.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.play_bottom.*
import org.fourthline.cling.support.model.PositionInfo
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity() {

    var suface: SurfaceView? = null
    var path: String? = null
    var mediaPlayler: MediaPlayer? = null
    private val positionInfo by lazy { PositionInfo() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        path = intent.getStringExtra(KeyValueUtils.PATH)
        initView()
        initBroadcastReceiver()
        startPlay(path!!)
    }

    private var broadcast: PlayBroadcastReceiver? = null

    private fun initBroadcastReceiver() {
        broadcast = PlayBroadcastReceiver()
        var intentFilter = IntentFilter()
        intentFilter.addAction(KeyValueUtils.PLAY)
        intentFilter.addAction(KeyValueUtils.PAUSE)
        intentFilter.addAction(KeyValueUtils.SET_VOLUME)
        registerReceiver(broadcast, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }

    private var audioServer: AudioManager? = null

    private fun initView() {
        mediaPlayler = MediaPlayer()
        audioServer = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioServer!!.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        mediaPlayler!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        initMediaPlayListen()
        suface = findViewById(R.id.surface_view)
        suface!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                mediaPlayler!!.setSurface(holder!!.surface)
            }
        })
        iv_play_pause.setOnClickListener {
            if (mediaPlayler!!.isPlaying) {
                pausePlay()
                PlayerObservable.instance.notifyObservers(false)
            } else {
                restartPlay()
                PlayerObservable.instance.notifyObservers(true)
            }
        }
        iv_value_left.setOnClickListener {
            var streamVolume = getVolume()
            if (streamVolume > 0) {
                streamVolume--
            }
            setVolume(streamVolume)
            PlayerObservable.instance.notifyObservers(streamVolume)
        }

        iv_value_right.setOnClickListener {
            var streamVolume = getVolume()
            var streamMaxVolume = audioServer!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            if (streamVolume < streamMaxVolume) {
                streamVolume++
            }
            setVolume(streamVolume)
            PlayerObservable.instance.notifyObservers(streamVolume)
        }
        seek_bar_progress.isClickable = false
        seek_bar_progress.isEnabled = false
        seek_bar_progress.isSelected = false
        seek_bar_progress.isFocusable = false
    }
    private var subscribe: Disposable? = null

    private fun initMediaPlayListen() {
        mediaPlayler!!.setOnPreparedListener {
            mediaPlayler!!.start()
            initVoiceValue()
            PlayerObservable.instance.notifyObservers(getVolume())
            subscribe?.dispose()
            subscribe = Observable.interval(1, TimeUnit.SECONDS).map {
                mediaPlayler!!.currentPosition
            }.observeOn(AndroidSchedulers.mainThread()).subscribe {
                val currentPosition = mediaPlayler!!.currentPosition
                val duration = mediaPlayler!!.duration
                initControlView(currentPosition, duration)
                positionInfo.relTime = tv_start.text as String?
                positionInfo.trackDuration = tv_end.text as String?
                PlayerObservable.instance.notifyObservers(positionInfo)
            }
        }
        mediaPlayler!!.setOnCompletionListener {
            subscribe?.dispose()
            mediaPlayler!!.stop()
            mediaPlayler!!.reset()
            println("播放完成")
            startPlay(path!!)
        }

        mediaPlayler!!.setOnErrorListener { mp, what, extra ->
            subscribe?.dispose()
            true
        }
    }

    private fun initControlView(currentPosition: Int, duration: Int) {
        tv_start.text = DateUtils.secToTime(currentPosition / 1000.toLong())
        tv_end.text = DateUtils.secToTime(duration / 1000.toLong())
        seek_bar_progress.max = duration
        seek_bar_progress.progress = currentPosition
    }

    private fun initVoiceValue() {
        val streamVolume = getVolume()
        val streamMaxVolume = audioServer!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        tv_sould_value.text = "$streamVolume/$streamMaxVolume"
    }

    private fun startPlay(path: String) {
        mediaPlayler!!.setDataSource(path)
        mediaPlayler!!.prepareAsync()
    }

    private fun pausePlay() {
        mediaPlayler!!.pause()
        iv_play_pause.setImageResource(R.mipmap.button_play)
    }

    private fun restartPlay() {
        if (!mediaPlayler!!.isPlaying) {
            mediaPlayler!!.start()
            iv_play_pause.setImageResource(R.mipmap.button_pause)
        }
    }

    private fun setVolume(currentPosition: Int) {
        audioServer!!.setStreamVolume(AudioManager.STREAM_MUSIC, currentPosition, 0)
        val streamMaxVolume = audioServer!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        tv_sould_value.text = "$currentPosition/$streamMaxVolume"
    }

    private fun getVolume(): Int {
        return audioServer!!.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    /**
     * 远程控制播放广播,控制当前播放器状态
     */
    inner class PlayBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println("onReceive")
            val action = intent?.action
            when (action) {
                KeyValueUtils.PLAY -> restartPlay()
                KeyValueUtils.PAUSE -> pausePlay()
                KeyValueUtils.SET_VOLUME -> {
                    val intExtra = intent.getIntExtra(KeyValueUtils.VOLUME, getVolume())
                    setVolume(intExtra)
                    PlayerObservable.instance.notifyObservers(intExtra)}
            }
        }
    }
}
