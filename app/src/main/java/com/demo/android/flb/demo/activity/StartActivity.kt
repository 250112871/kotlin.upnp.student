package com.demo.android.flb.demo.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.MediaStore
import com.demo.android.flb.demo.dms.ContentItem
import com.demo.android.flb.demo.dms.ContentTree
import com.demo.android.flb.demo.dms.MediaServer
import com.demo.android.flb.demo.utils.KeyValueUtils.Companion.PNG_SUFFIX
import com.demo.android.flb.demo.utils.KeyValueUtils.Companion.VIDEO
import com.demo.android.flb.demo.utils.KeyValueUtils.Companion.VIDEO_HEAD
import com.demo.android.flb.demo.utils.KeyValueUtils.Companion.VIDEO_THUMBNAIL_PREFIX
import com.demo.android.flb.demo.utils.KeyValueUtils.Companion.VIDEO_THUMB_PATH
import com.demo.android.flb.demo.utils.FileUtils
import com.demo.android.flb.demo.utils.Lg
import com.demo.android.flb.kotlin_upnp_demo.R
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.fourthline.cling.support.model.DIDLObject
import org.fourthline.cling.support.model.Res
import org.fourthline.cling.support.model.container.Container
import org.fourthline.cling.support.model.item.Item
import java.io.File
import java.io.FileOutputStream
import java.net.InetAddress
import java.net.URI
import java.net.UnknownHostException

/**
 * @author Created by 25011 on 2018/7/30.
 * 1检查是否存在资源目录，不存在就创建
 * 2遍历设备所有媒体文件
 * 3用遍历后的媒体文件创建快捷图标并存储
 * 4完成以上步骤后跳到MainActivity
 */
class StartActivity : Activity() {
    private var filepath: String
    private var videoList: ArrayList<Any>
    private var contentTree: ContentTree? = null

    init {
        contentTree = ContentTree.instance
        filepath = FileUtils.createFile(VIDEO_THUMB_PATH)
        videoList = ArrayList(10)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)
        initData()
    }


    private fun initData() {
        val thumbList = queryVideo()
        if (thumbList != null) {
            val container = Container()
            container.id = ContentTree.VIDEO_ID
            container.parentID = ContentTree.ROOT_ID
            container.childCount = thumbList.size
            container.title = VIDEO
            container.clazz = DIDLObject.Class("object.container")
            ContentTree.instance.rootElement!!.container!!.addContainer(container)

            Observable.fromIterable(thumbList).observeOn(Schedulers.io()).subscribe({
                val createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(it.path, MediaStore.Video.Thumbnails.MINI_KIND)
                val savePath = filepath + File.separator + VIDEO_THUMBNAIL_PREFIX + it._id + PNG_SUFFIX
                val format = Bitmap.CompressFormat.PNG
                val file = File(savePath)
                val fileOutPut = FileOutputStream(file)
                if (createVideoThumbnail.compress(format, 70, fileOutPut)) {
                    fileOutPut.flush()
                    fileOutPut.close()
                }
                val item = Item()
                item.id = VIDEO_HEAD + it._id.toString()
                item.parentID = ContentTree.VIDEO_ID
                item.title = it.title
                val res = Res(it.minType, -1, "-1", -1, "http://" + BaseApplication.instance!!.addressPath + ":${MediaServer.PORT}" + File.separator + VIDEO + File.separator + item.id)
                item.addResource(res)
                val director = DIDLObject.Property.UPNP.ALBUM_ART_URI(URI.create("http://" + BaseApplication.instance!!.addressPath + ":${MediaServer.PORT}" + File.separator + VIDEO + File.separator + savePath))
                item.properties = listOf(director)
                item.clazz = DIDLObject.Class("object.item.videoItem")
                container.addItem(item)
                contentTree!!.addContent(item.id, ContentItem(item.id, item, it.path))
            }, { }, {
                contentTree!!.addContent(container.id, ContentItem(container.id, container))
                Lg.i("共获媒体文件数${contentTree!!.getContentCount()}")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            })
        }

        Observable.create<Int> {
            it.onNext(1)
            it.onComplete()
        }.observeOn(Schedulers.newThread()).subscribe {
            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipAddress = wifiInfo.ipAddress

            val iNetAddress: InetAddress
            try {
                iNetAddress = InetAddress.getByName(String.format("%d.%d.%d.%d",
                        ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff,
                        ipAddress shr 24 and 0xff))

                val hostName = iNetAddress.hostName
                val hostAddress = iNetAddress.hostAddress
                BaseApplication.instance!!.addressPath = hostAddress
                Lg.i("获取网络地址 hostName:$hostName  hostAddress:${BaseApplication.instance!!.addressPath}")
            } catch (e: UnknownHostException) {
            }
        }
    }

    private fun queryVideo(): ArrayList<ThumbBean>? {
        val query = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.MIME_TYPE),
                null,
                null,
                null
        )

        var thumbList: ArrayList<ThumbBean>? = null
        if (query.moveToFirst()) {
            thumbList = ArrayList(20)
            do {
                val id = query.getInt(query.getColumnIndex(MediaStore.Video.Media._ID))
                val path = query.getString(query.getColumnIndex(MediaStore.Video.Media.DATA))
                val title = query.getString(query.getColumnIndex(MediaStore.Video.Media.TITLE))
                val mineType = query.getString(query.getColumnIndex(MediaStore.Video.Media.MIME_TYPE))
                thumbList.add(ThumbBean(id, path, title, mineType))
            } while (query.moveToNext())
            query.close()
        }

        return thumbList
    }
}

data class ThumbBean(val _id: Int, val path: String, val title: String, val minType: String)