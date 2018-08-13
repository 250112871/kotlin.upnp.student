package com.demo.android.flb.demo.base

import org.fourthline.cling.support.model.PositionInfo

/**
 * @author Created by 25011 on 2018/8/11.
 * 播放器观察者
 * 观察声音状态，播放状态，断点状态变化
 */
interface PlayerObserver {
    fun update(o: PositionInfo)
    fun upVoice(value: Int)
    fun upPlayState(isPlaying: Boolean)
}