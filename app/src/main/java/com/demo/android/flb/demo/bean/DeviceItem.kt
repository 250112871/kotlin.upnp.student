package com.demo.android.flb.demo.bean

import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice

/**
 * @author Created by 25011 on 2018/8/1.
 * 设备展示用的Bean
 * 如果设备名称相同则认为两个设备为同一个设备
 */
class DeviceItem constructor(val name: String) {

    var localDevice: LocalDevice? = null
    var remoteDevice: RemoteDevice? = null
    var isRemoteDevice: Boolean = false

    constructor(name: String, localDevice: LocalDevice) : this(name) {
        this.localDevice = localDevice
        isRemoteDevice = false
    }

    constructor(name: String, localDevice: RemoteDevice) : this(name) {
        this.remoteDevice = localDevice
        isRemoteDevice = true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is DeviceItem) {
            name == other.name
        } else {
            false
        }
    }
}