package com.demo.android.flb.demo.utils

/**
 * @author Created by 25011 on 2018/8/11.
 * 时间相关工具类
 */
class DateUtils {
    companion object {

        fun secToTime(paramLong: Long): String {
            val time = paramLong.toInt()
            val timeStr: String?
            val hour: Int
            var minute: Int
            val second: Int
            if (time <= 0) {
                return "00:00:00"
            } else {
                minute = time / 60
                if (minute < 60) {
                    second = time % 60
                    timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second)
                } else {
                    hour = minute / 60
                    if (hour > 99) {
                        return "99:59:59"
                    }
                    minute %= 60
                    second = time - hour * 3600 - minute * 60
                    timeStr = (unitFormat(hour) + ":" + unitFormat(minute) + ":"
                            + unitFormat(second))
                }
            }
            return timeStr
        }

        private fun unitFormat(i: Int): String {
            return if (i in 0..9) {
                "0" + Integer.toString(i)
            } else {
                i.toString()
            }
        }

        fun getRealTime(paramString: String): Int {
            val i = paramString.indexOf(":")
            var j = 0
            if (i > 0) {
                val arrayOfString = paramString.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (arrayOfString.size == 3) {
                    j = Integer.parseInt(arrayOfString[2]) + 60 * Integer.parseInt(arrayOfString[1]) + 3600 * Integer.parseInt(arrayOfString[0])
                }
            }
            return j
        }
    }
}