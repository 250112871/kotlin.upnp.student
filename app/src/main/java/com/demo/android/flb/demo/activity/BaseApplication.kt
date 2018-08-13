package com.demo.android.flb.demo.activity

import android.app.Application
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.model.meta.Device

/**
 * @author Created by 25011 on 2018/7/31.
 * 1记录当前选择的渲染设备，和媒体提供设备
 * 2记录本机的portal地址
 */
class BaseApplication : Application() {
    var addressPath: String? = null
    var androidService: AndroidUpnpService? = null
    var currentService:Device<*,*,*>? = null
    var currentDevice:Device<*,*,*>? = null
    companion object {
        var instance: BaseApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}