package com.demo.android.flb.demo.utils

import android.util.Log

/**
 * @author Created by 25011 on 2018/7/19.
 * Log 管理类
 */
object Lg {
    private var isDebug = true
    private const val TAG = "[LB]: "
    private const val STACK_TRACE = 3

    fun i(tag: String, msg: String) {
        if (isDebug) {
            Log.i(tag, "[${targetStackTraceMSg()}]:\t ----> $msg <----")
        }
    }
    fun i(msg: String) {
        i(TAG + getFileName(), msg)
    }

    private fun targetStackTraceMSg(): String {
        val targetStackTraceElement = getTargetStackTraceElement()
        return if (targetStackTraceElement != null) {
            "at ${targetStackTraceElement.className}.${targetStackTraceElement.methodName}(${targetStackTraceElement.fileName}:${targetStackTraceElement.lineNumber})"
        } else {
            ""
        }
    }

    private fun getTargetStackTraceElement(): StackTraceElement? {
        var targetStackTrace: StackTraceElement? = null
        var shouldTrace = false
        val stackTrace = Thread.currentThread().stackTrace
        for (stackTraceElement in stackTrace) {
            val isLogMethod = stackTraceElement.className == Lg::class.java.name
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement
                break
            }
            shouldTrace = isLogMethod
        }
        return targetStackTrace
    }

    private fun getFileName(): String {
        return Throwable().stackTrace[STACK_TRACE].fileName
    }
}
