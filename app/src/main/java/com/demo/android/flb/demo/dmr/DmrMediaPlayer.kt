package com.demo.android.flb.demo.dmr

import android.content.Context
import android.content.Intent
import com.demo.android.flb.demo.base.PlayerObservable
import com.demo.android.flb.demo.base.PlayerObserver
import com.demo.android.flb.demo.utils.KeyValueUtils
import com.demo.android.flb.demo.utils.FileUtils
import com.demo.android.flb.demo.utils.Lg
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable
import org.fourthline.cling.support.lastchange.LastChange
import org.fourthline.cling.support.model.*
import java.net.URI

/**
 * @author Created by 25011 on 2018/8/10.
 * AvTransFrom 和播放器的间层 主要通过广播通知播放器进行操作
 * 目前实现部分功能
 */
class DmrMediaPlayer constructor(context: Context, unsignedIntegerFourBytes: UnsignedIntegerFourBytes, avTransportLastChange: LastChange,
                                 renderingControlLastChange: LastChange?) : PlayerObserver {
    private var playStateInfo = TransportInfo(TransportState.NO_MEDIA_PRESENT)
    private var unsignedIntegerFourBytes: UnsignedIntegerFourBytes? = null
    private var mediaInfo = MediaInfo()
    private val playPosition = PositionInfo()
    private var avTransportLastChange: LastChange? = null
    private var renderingControlLastChange: LastChange? = null
    private var context: Context? = null
    private var voiceValue: UnsignedIntegerTwoBytes? = UnsignedIntegerTwoBytes(0)

    init {
        this.unsignedIntegerFourBytes = unsignedIntegerFourBytes
        this.avTransportLastChange = avTransportLastChange
        this.renderingControlLastChange = renderingControlLastChange
        this.context = context
        PlayerObservable.instance.addObserver(this)
    }

    override fun upPlayState(isPlaying: Boolean) = if (isPlaying) {
        transportStateChanged(TransportState.PLAYING)
    } else {
        transportStateChanged(TransportState.PAUSED_PLAYBACK)
    }

    override fun upVoice(value: Int) {
        voiceValue = UnsignedIntegerTwoBytes(value.toLong())
    }

    override fun update(o: PositionInfo) {
        playPosition.relTime = o.relTime
        playPosition.trackDuration = o.trackDuration
    }


    fun getPositionInfo(): PositionInfo {
        return playPosition
    }

    fun play() {
        playStateInfo = TransportInfo(TransportState.PLAYING)
        val intent = Intent(KeyValueUtils.PLAY)
        context!!.sendBroadcast(intent)
    }

    fun setAVTransportURI(currentURI: String?, currentURIMetaData: String?) {
        Lg.i("setAVTransportURI")
        transportStateChanged(TransportState.PLAYING)
        mediaInfo = MediaInfo(currentURI, currentURIMetaData)
        playPosition.relTime = "00:00:00"
        playPosition.trackDuration = "00:00:00"
        val create = URI(currentURI)
        val avTransportURI = AVTransportVariable.AVTransportURI(create)
        val currentTrackURI = AVTransportVariable.CurrentTrackURI(create)
        avTransportLastChange?.setEventedValue(unsignedIntegerFourBytes, avTransportURI, currentTrackURI)
        startPlayerServer(currentURI, currentURIMetaData)
    }

    fun getTransportInfo(): TransportInfo {
        return playStateInfo
    }

    fun pause() {
        transportStateChanged(TransportState.PAUSED_PLAYBACK)
        val intent = Intent(KeyValueUtils.PAUSE)
        context!!.sendBroadcast(intent)
    }

    private fun startPlayerServer(currentURI: String?, currentURIMetaData: String?) {
        val fileSuffix = FileUtils.getFileSuffix(currentURI!!)
        val intent = Intent(context, RenderPlayerServer::class.java)
        intent.putExtra(KeyValueUtils.PATH, currentURI)
        intent.putExtra(KeyValueUtils.META_DATA, currentURIMetaData)
        intent.putExtra(KeyValueUtils.TYPE, fileSuffix.replace(".", ""))
        context!!.startService(intent)
    }

    private fun transportStateChanged(currentTransportState: TransportState) {
        playStateInfo = when (currentTransportState) {
            TransportState.PLAYING -> TransportInfo(TransportState.PLAYING)
            TransportState.STOPPED -> TransportInfo(TransportState.STOPPED)
            TransportState.PAUSED_PLAYBACK -> TransportInfo(TransportState.PAUSED_PLAYBACK)
            else -> TransportInfo(TransportState.NO_MEDIA_PRESENT)
        }
    }

    fun getVolume(): UnsignedIntegerTwoBytes {
        return voiceValue!!
    }

    fun setMute() {
        setVolume(UnsignedIntegerTwoBytes(0))
    }

    fun setVolume(desiredVolume: UnsignedIntegerTwoBytes) {
        val intent = Intent(KeyValueUtils.SET_VOLUME)
        intent.putExtra(KeyValueUtils.VOLUME, desiredVolume.value.toInt())
        context!!.sendBroadcast(intent)
    }
}