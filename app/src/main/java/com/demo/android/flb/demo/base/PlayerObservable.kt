package com.demo.android.flb.demo.base

import org.fourthline.cling.support.model.PositionInfo

/**
 * @author Created by 25011 on 2018/8/11.
 * 播放器被观察者
 * 当声音状态，播放状态，断点状态发生变化时通知观察者
 */
class PlayerObservable private constructor() {
    companion object {
        val instance by lazy { PlayerObservable() }
    }

    private val list: ArrayList<PlayerObserver> = ArrayList()

    fun addObserver(playerObserver: PlayerObserver) {
        removeObserver(playerObserver)
        list.add(playerObserver)
    }

    private fun removeObserver(playerObserver: PlayerObserver) {
        if (list.contains(playerObserver)) {
            list.remove(playerObserver)
        }
    }

    fun notifyObservers(positionInfo: PositionInfo) {
        for (playerObserver in list) {
            playerObserver.update(positionInfo)
        }
    }

    fun notifyObservers(voice: Int) {
        for (playerObserver in list) {
            playerObserver.upVoice(voice)
        }
    }

    fun notifyObservers(isPlaying: Boolean) {
        for (playerObserver in list) {
            playerObserver.upPlayState(isPlaying)
        }
    }
}