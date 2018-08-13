package com.demo.android.flb.demo.dmr

import android.content.Context
import android.os.Build
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder
import org.fourthline.cling.model.DefaultServiceManager
import org.fourthline.cling.model.meta.DeviceDetails
import org.fourthline.cling.model.meta.DeviceIdentity
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.LocalService
import org.fourthline.cling.model.types.UDADeviceType
import org.fourthline.cling.model.types.UDN
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser
import org.fourthline.cling.support.lastchange.LastChange
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Created by 25011 on 2018/8/7.
 * 设备渲染设备包括
 * 1支持的播放协议 ConnectionManagerService
 * 2视频控制服务   AVTransportService
 * 3视频控制服务  AudioRenderingControl
 * 4持有播放控制工具的引用
 */
class MediaRange constructor(context: Context, number: Int) {
    private var playerNumber: Int = 0
    var context: Context? = null

    init {
        playerNumber = number
        this.context = context
    }

    var device: LocalDevice? = null

    init {
        device = createLocalDevice()
    }

    companion object {
        const val TYPE: String = "MediaRenderer"
    }

    private fun createLocalDevice(): LocalDevice {
        val udn = DeviceIdentity(UDN(TYPE + Build.BOARD))
        val type = UDADeviceType(TYPE)
        val details = DeviceDetails("${Build.BOARD}--->$TYPE")
        val map = ConcurrentHashMap<UnsignedIntegerFourBytes, DmrMediaPlayer>()
        val renderingControlLastChange = LastChange(AVTransportLastChangeParser())
        val audioRenderLastChange = LastChange(RenderingControlLastChangeParser())
        for (i in 1..playerNumber) {
            val unsignedIntegerFourBytes = UnsignedIntegerFourBytes((i - 1).toLong())
            map[unsignedIntegerFourBytes] = DmrMediaPlayer(context!!, unsignedIntegerFourBytes, renderingControlLastChange, audioRenderLastChange)
        }


        val connectService: LocalService<MediaConnectService> = AnnotationLocalServiceBinder().read(MediaConnectService::class.java) as LocalService<MediaConnectService>
        val defaultServiceManager = DefaultServiceManager<MediaConnectService>(connectService, MediaConnectService::class.java)
        connectService.manager = defaultServiceManager

        val rangeControl: LocalService<AudioRenderControl> = AnnotationLocalServiceBinder().read(AudioRenderControl::class.java) as LocalService<AudioRenderControl>
        val controlManager = object : LastChangeAwareServiceManager<AudioRenderControl>(rangeControl, RenderingControlLastChangeParser()) {
            override fun createServiceInstance(): AudioRenderControl {
                return AudioRenderControl(audioRenderLastChange, map)
            }
        }
        rangeControl.manager = controlManager

        val avTransService: LocalService<AvTransFrom> = AnnotationLocalServiceBinder().read(AvTransFrom::class.java) as LocalService<AvTransFrom>
        val lastChangeAwareServiceManager = object : LastChangeAwareServiceManager<AvTransFrom>(avTransService, AVTransportLastChangeParser()) {
            override fun createServiceInstance(): AvTransFrom {
                return AvTransFrom(renderingControlLastChange, map)
            }
        }
        avTransService.manager = lastChangeAwareServiceManager

        val services = arrayOf(connectService, avTransService, rangeControl)
        return LocalDevice(udn, type, details, services)
    }

}