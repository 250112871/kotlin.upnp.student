package com.demo.android.flb.demo.dms

import android.os.Build
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder
import org.fourthline.cling.model.DefaultServiceManager
import org.fourthline.cling.model.meta.DeviceDetails
import org.fourthline.cling.model.meta.DeviceIdentity
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.LocalService
import org.fourthline.cling.model.types.UDADeviceType
import org.fourthline.cling.model.types.UDN

/**
 * @author Created by 25011 on 2018/8/2.
 * 媒体内容服务设备，包括内容浏览服务，和 内部服务器用于将内容求分享出去
 */
class MediaServer {
    var localDevice: LocalDevice? = null

    companion object {
        const val TYPE: String = "MediaServer"
        const val PORT: String = "9090"
    }

    init {
        val type = UDADeviceType(TYPE)
        val identity = DeviceIdentity(UDN(TYPE + Build.BOARD))
        val details = DeviceDetails(Build.BOARD + "-->$TYPE")
        val service: LocalService<ContentDirectoryService> = AnnotationLocalServiceBinder().read(ContentDirectoryService::class.java) as LocalService<ContentDirectoryService>
        service.manager = DefaultServiceManager<ContentDirectoryService>(service, ContentDirectoryService::class.java)
        localDevice = LocalDevice(identity, type, details, service)
        startServer()
    }

    private fun startServer() {
        Thread(HttpServer(PORT.toInt())).start()
    }

}