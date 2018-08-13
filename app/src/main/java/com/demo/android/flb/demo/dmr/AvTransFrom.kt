package com.demo.android.flb.demo.dmr

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes
import org.fourthline.cling.support.avtransport.AbstractAVTransportService
import org.fourthline.cling.support.lastchange.LastChange
import org.fourthline.cling.support.model.*

/**
 * @author Created by 25011 on 2018/8/10.
 * 媒体资源控制
 * 关于媒体操作，播放，暂停，断点，当前播放状态，快进，快退，获取媒体信息，设置下一个内容等
 * 暂时没有全部实现
 */
class AvTransFrom constructor(lastChange: LastChange, map: Map<UnsignedIntegerFourBytes, DmrMediaPlayer>) : AbstractAVTransportService(lastChange) {
    private var map: Map<UnsignedIntegerFourBytes, DmrMediaPlayer>? = null
    private var mediaInfo: MediaInfo? = null

    init {
        this.map = map
    }

    private fun getCurrentMediaPlayer(instanceId: UnsignedIntegerFourBytes?): DmrMediaPlayer? {
        return map!![instanceId]
    }

    override fun getPositionInfo(instanceId: UnsignedIntegerFourBytes?): PositionInfo? {
        return getCurrentMediaPlayer(instanceId)!!.getPositionInfo()
    }

    override fun getTransportSettings(instanceId: UnsignedIntegerFourBytes?): TransportSettings? {
        return null
    }

    override fun getCurrentInstanceIds(): Array<UnsignedIntegerFourBytes>? {
        return null
    }

    override fun record(instanceId: UnsignedIntegerFourBytes?) {
    }

    override fun previous(instanceId: UnsignedIntegerFourBytes?) {
    }

    override fun seek(instanceId: UnsignedIntegerFourBytes?, unit: String?, target: String?) {
    }

    override fun play(instanceId: UnsignedIntegerFourBytes?, speed: String?) {
        getCurrentMediaPlayer(instanceId)!!.play()
    }

    override fun next(instanceId: UnsignedIntegerFourBytes?) {
    }

    override fun setAVTransportURI(instanceId: UnsignedIntegerFourBytes?, currentURI: String?, currentURIMetaData: String?) {
        mediaInfo = MediaInfo(currentURI, currentURIMetaData)
        getCurrentMediaPlayer(instanceId)!!.setAVTransportURI(currentURI, currentURIMetaData)
    }

    override fun getTransportInfo(instanceId: UnsignedIntegerFourBytes?): TransportInfo? {
        return getCurrentMediaPlayer(instanceId)!!.getTransportInfo()

    }

    override fun getMediaInfo(instanceId: UnsignedIntegerFourBytes?): MediaInfo? {
        return mediaInfo
    }

    override fun pause(instanceId: UnsignedIntegerFourBytes?) {
        getCurrentMediaPlayer(instanceId)!!.pause()
    }

    override fun getDeviceCapabilities(instanceId: UnsignedIntegerFourBytes?): DeviceCapabilities? {
        return null
    }

    override fun setNextAVTransportURI(instanceId: UnsignedIntegerFourBytes?, nextURI: String?, nextURIMetaData: String?) {
    }

    override fun stop(instanceId: UnsignedIntegerFourBytes?) {
    }

    override fun getCurrentTransportActions(instanceId: UnsignedIntegerFourBytes?): Array<TransportAction>? {
        return null
    }

    override fun setRecordQualityMode(instanceId: UnsignedIntegerFourBytes?, newRecordQualityMode: String?) {
    }

    override fun setPlayMode(instanceId: UnsignedIntegerFourBytes?, newPlayMode: String?) {
    }
}