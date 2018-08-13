package com.demo.android.flb.demo.utils

import android.os.Environment
import java.io.File

/**
 * @author Created by 25011 on 2018/7/30.
 * 关于文件操作的工具类
 */
class FileUtils {
    companion object {
        fun createFile(path: String): String {
            val rootPath: String? = getPath()

            val file = File(rootPath + File.separator + path)
            if (!file.exists()) {
                file.mkdir()
                Lg.i("创建目录   path:$file")
            } else {
                Lg.i("目录   path:$file 已存在")
            }
            return file.toString()
        }

        private fun getPath(): String = when (Environment.getExternalStorageState()) {
            Environment.MEDIA_MOUNTED -> Environment.getExternalStorageDirectory().toString()
            else -> Environment.getDownloadCacheDirectory().toString()
        }

        /**
         * 获取文件后缀
         */
        fun getFileSuffix(pathName: String): String {
            var suffix = ""
            if (pathName.contains(KeyValueUtils.VIDEO)) {
                suffix = KeyValueUtils.MP4
            }
            return suffix
        }
    }
}