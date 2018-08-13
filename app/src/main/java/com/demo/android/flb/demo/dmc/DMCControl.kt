package com.demo.android.flb.demo.dmc

import android.os.Handler
import com.demo.android.flb.demo.base.IControlView
import com.demo.android.flb.demo.utils.FileUtils
import com.demo.android.flb.demo.utils.Lg
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.support.avtransport.callback.*
import org.fourthline.cling.support.connectionmanager.callback.GetProtocolInfo
import org.fourthline.cling.support.model.*
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume

/**
 * @author Created by 25011 on 2018/8/10.
 * controlFragment 逻辑控制类
 */
class DMCControl(androidService: AndroidUpnpService?, currentService: Device<*, *, *>?, currentDevice: Device<*, *, *>?) {
    private var service: Device<*, *, *>? = null
    private var device: Device<*, *, *>? = null
    private var androidService: AndroidUpnpService? = null
    private var handler: Handler? = null
    var mView: IControlView? = null

    init {
        this.androidService = androidService
        service = currentService
        device = currentDevice
        handler = Handler()
    }

    fun attachView(view: IControlView) {
        mView = view
    }

    fun getProtocolInfo(path: String, mediaData: String) {
        val suffix = FileUtils.getFileSuffix(path)
        val findService: Service<*, *> = device!!.findService(UDAServiceType("ConnectionManager"))
        val callback = object : GetProtocolInfo(findService) {
            override fun received(actionInvocation: ActionInvocation<out Service<*, *>>?, sinkProtocolInfos: ProtocolInfos?, sourceProtocolInfos: ProtocolInfos?) {
                Lg.i("播放协议获取成功")
                var isContain = false
                if (sinkProtocolInfos != null) {
                    for (info in sinkProtocolInfos) {
                        if (info.contentFormat.contains(suffix)) {
                            isContain = true
                            Lg.i("当前资源符合播放协议")
                            break
                        }
                    }
                }
                if (isContain) {
                    setUrl(path, mediaData)
                }
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
            }

        }
        androidService!!.controlPoint.execute(callback)
    }

    private fun setUrl(path: String, mediaData: String) {
        Lg.i("设置播放连接 path：$path mediaData:$mediaData")
        val avTransportService: Service<*, *> = device!!.findService(UDAServiceType("AVTransport"))
        androidService!!.controlPoint.execute(object : SetAVTransportURI(avTransportService, path, mediaData) {
            override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                Lg.i("播放连接设置成功，获取状态信息")
                getPlayerInfo()
                getBreakPotInfo()
                initVoice()
                getCurrentPlayState()
                super.success(invocation)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                Lg.i("播放连接设置失败")
            }
        })
    }

    fun getPlayerInfo() {
        val findService = device!!.findService(UDAServiceType("AVTransport"))
        androidService!!.controlPoint.execute(object : GetMediaInfo(findService) {
            override fun received(invocation: ActionInvocation<out Service<*, *>>?, mediaInfo: MediaInfo?) {
                if (mediaInfo != null) {
                    val currentURI = mediaInfo.currentURI
                    println("获取当前正在播放的节目 地址：$currentURI")
                } else {
                    println("获取当前正在播放的节目 地址 null ")
                }
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {

            }

        })
    }

    private val getBreakPot = Runnable { getBreakPotInfo() }
    private fun getBreakPotInfo() {
        handler?.removeCallbacks(getBreakPot)
        handler?.postDelayed(getBreakPot, 1000)

        val findService = device!!.findService(UDAServiceType("AVTransport"))
        androidService!!.controlPoint.execute(object : GetPositionInfo(findService) {
            override fun received(invocation: ActionInvocation<out Service<*, *>>?, positionInfo: PositionInfo?) {
                if (positionInfo != null) {
                    if (mView != null) {
                        mView!!.initPlayTime(positionInfo.relTime, positionInfo.trackDuration)
                    }
                }
            }
            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
            }
        })
    }

    private var getCurrentPlayStateRunnable = Runnable { getCurrentPlayState() }
    private fun getCurrentPlayState() {
        handler?.removeCallbacks(getCurrentPlayStateRunnable)
        handler?.postDelayed(getCurrentPlayStateRunnable, 1000)
        val findService = device!!.findService(UDAServiceType("AVTransport"))
        androidService!!.controlPoint.execute(object : GetTransportInfo(findService) {
            override fun received(invocation: ActionInvocation<out Service<*, *>>?, transportInfo: TransportInfo?) {
                if (transportInfo != null) {
                    when (transportInfo.currentTransportState) {
                        TransportState.PLAYING -> {
                            mView?.intPlayStatue(true)
                        }
                        TransportState.PAUSED_PLAYBACK -> {
                            mView?.intPlayStatue(false)
                        }
                        else -> {

                        }
                    }
                }
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
            }

        })
    }

    private fun onKeyPlayButton() {
        val findService = device!!.findService(UDAServiceType("AVTransport"))
        androidService!!.controlPoint.execute(object : GetTransportInfo(findService) {
            override fun received(invocation: ActionInvocation<out Service<*, *>>?, transportInfo: TransportInfo?) {
                if (transportInfo != null) {
                    when (transportInfo.currentTransportState) {
                        TransportState.PLAYING -> {
                            pause()
                        }
                        TransportState.PAUSED_PLAYBACK -> {
                            player()
                        }
                        else -> {
                        }
                    }
                }
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
            }

        })
    }

    private fun player() {
        val findService = device!!.findService(UDAServiceType("AVTransport"))
        androidService!!.controlPoint.execute(object : Play(findService) {
            override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                mView?.intPlayStatue(true)
                super.success(invocation)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
            }
        })
    }

    private fun pause() {
        val findService = device!!.findService(UDAServiceType("AVTransport"))
        androidService!!.controlPoint.execute(object : Pause(findService) {
            override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                mView?.intPlayStatue(false)
                super.success(invocation)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
            }
        })
    }

    fun setVoice(voice: Int) {
        if (voice in 0..16) {
            val findService = device!!.findService(UDAServiceType("RenderingControl"))
            androidService!!.controlPoint.execute(object : SetVolume(findService, voice.toLong()) {

                override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                }

            })
        }
    }

    fun onKeyPlay() {
        onKeyPlayButton()
    }

    private var initVoiceRunnable = Runnable { initVoice() }
    fun initVoice() {
        handler?.removeCallbacks(initVoiceRunnable)
        handler?.postDelayed(initVoiceRunnable, 1000)
        val findService = device!!.findService(UDAServiceType("RenderingControl"))
        androidService!!.controlPoint.execute(object : GetVolume(findService) {
            override fun received(actionInvocation: ActionInvocation<out Service<*, *>>?, currentVolume: Int) {
                mView?.initVoice(currentVolume)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
            }
        })
    }


}