package com.demo.android.flb.demo.dmr

import org.fourthline.cling.model.types.ErrorCode
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes
import org.fourthline.cling.support.lastchange.LastChange
import org.fourthline.cling.support.model.Channel
import org.fourthline.cling.support.renderingcontrol.AbstractAudioRenderingControl
import org.fourthline.cling.support.renderingcontrol.RenderingControlException

/**
 * @author Created by 25011 on 2018/8/12.
 * 媒体控制服务
 * 用于声音相关的控制
 */
class AudioRenderControl constructor(lastChange: LastChange, map: Map<UnsignedIntegerFourBytes, DmrMediaPlayer>) : AbstractAudioRenderingControl(lastChange) {
    private var map: Map<UnsignedIntegerFourBytes, DmrMediaPlayer>? = null

    init {
        this.map = map
    }

    override fun getCurrentInstanceIds(): Array<UnsignedIntegerFourBytes> {
        return map!!.keys.toTypedArray()
    }

    override fun getMute(instanceId: UnsignedIntegerFourBytes?, channelName: String?): Boolean {
        checkChannel(channelName!!)
        return getCurrentPlayer(instanceId).getVolume().value == 0L
    }

    override fun setMute(instanceId: UnsignedIntegerFourBytes?, channelName: String?, desiredMute: Boolean) {
        checkChannel(channelName!!)
        getCurrentPlayer(instanceId).setMute()
    }

    override fun getVolume(instanceId: UnsignedIntegerFourBytes?, channelName: String?): UnsignedIntegerTwoBytes {
        checkChannel(channelName!!)
        return getCurrentPlayer(instanceId).getVolume()
    }

    override fun setVolume(instanceId: UnsignedIntegerFourBytes?, channelName: String?, desiredVolume: UnsignedIntegerTwoBytes?) {
        checkChannel(channelName!!)
        return getCurrentPlayer(instanceId).setVolume(desiredVolume!!)
    }

    override fun getCurrentChannels(): Array<Channel> {
        return arrayOf(Channel.Master)
    }

    private fun getCurrentPlayer(instanceId: UnsignedIntegerFourBytes?): DmrMediaPlayer {
        return map!![instanceId]!!
    }

    private fun checkChannel(channelName: String) {
        if (getChannel(channelName) != Channel.Master) {
            throw RenderingControlException(ErrorCode.ARGUMENT_VALUE_INVALID, "Unsupported audio channel: $channelName")
        }
    }
}