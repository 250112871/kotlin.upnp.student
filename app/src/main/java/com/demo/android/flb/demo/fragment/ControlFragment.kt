package com.demo.android.flb.demo.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demo.android.flb.demo.activity.BaseApplication
import com.demo.android.flb.demo.base.IControlView
import com.demo.android.flb.demo.dmc.DMCControl
import com.demo.android.flb.demo.utils.DateUtils.Companion.getRealTime
import com.demo.android.flb.demo.utils.KeyValueUtils.Companion.CONTROL_PLAY_UPDATE
import com.demo.android.flb.demo.utils.KeyValueUtils.Companion.META_DATA
import com.demo.android.flb.demo.utils.KeyValueUtils.Companion.PATH
import com.demo.android.flb.demo.utils.Lg
import com.demo.android.flb.kotlin_upnp_demo.R
import kotlinx.android.synthetic.main.play_bottom.*

/**
 * 媒体控制Fragment
 * 同步媒体播放的状态
 */
class ControlFragment : Fragment(), IControlView {
    private var isPlaying = false
    private val broadcastReceiver = ResourceBroadCastReceiver()
    private var dmcControl: DMCControl? = null

    override fun initVoice(voice: Int) {
        if (tv_sould_value.text != voice.toString()) {
            Lg.i("声音发生变化 voice:$voice")
            activity?.runOnUiThread {
                tv_sould_value.text = "$voice"
            }
        }
    }

    override fun intPlayStatue(isPlaying: Boolean) {
        if (this.isPlaying != isPlaying) {
            this.isPlaying = isPlaying
            val res = if (isPlaying) {
                R.mipmap.button_pause
            } else {
                R.mipmap.button_play
            }
            activity?.runOnUiThread {
                iv_play_pause.setImageResource(res)
            }
            Lg.i("更新播放状态")
        }
    }

    override fun initPlayTime(startTime: String, duration: String) {
        if (tv_start.text != startTime) {
            activity?.runOnUiThread {
                tv_start.text = startTime
                tv_end.text = duration
                seek_bar_progress.max = getRealTime(duration)
                seek_bar_progress.progress = getRealTime(startTime)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        seek_bar_progress.isClickable = false
        seek_bar_progress.isEnabled = false
        seek_bar_progress.isSelected = false
        seek_bar_progress.isFocusable = false
        iv_play_pause.setOnClickListener { dmcControl!!.onKeyPlay() }
        iv_value_left.setOnClickListener { dmcControl!!.setVoice(tv_sould_value.text.toString().toInt() - 1) }
        iv_value_right.setOnClickListener { dmcControl!!.setVoice(tv_sould_value.text.toString().toInt() + 1) }
    }

    fun initData() {
        val currentService = BaseApplication.instance!!.currentService
        val currentDevice = BaseApplication.instance!!.currentDevice
        val androidService = BaseApplication.instance!!.androidService
        dmcControl = DMCControl(androidService, currentService, currentDevice)
        dmcControl!!.attachView(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val intentFilter = IntentFilter()
        intentFilter.addAction(CONTROL_PLAY_UPDATE)
        activity?.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onDetach() {
        super.onDetach()
        activity?.unregisterReceiver(broadcastReceiver)
    }

    private fun upData(path: String, mediaData: String) {
        dmcControl!!.getProtocolInfo(path, mediaData)
    }

    inner class ResourceBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            Lg.i("接受到广播 action:$action")
            when (action) {
                CONTROL_PLAY_UPDATE -> {
                    upData(intent.getStringExtra(PATH), intent.getStringExtra(META_DATA))
                }
            }
        }
    }
}
