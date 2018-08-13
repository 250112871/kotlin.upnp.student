package com.demo.android.flb.demo.base

/**
 * @author Created by 25011 on 2018/8/12.
 * 控制Fragment界面接口
 */
interface IControlView {
    fun initPlayTime(startTime: String, duration: String)
    fun intPlayStatue(isPlaying: Boolean)
    fun initVoice(voice: Int)
}