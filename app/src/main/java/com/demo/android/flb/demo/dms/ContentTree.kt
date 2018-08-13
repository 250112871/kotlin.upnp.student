package com.demo.android.flb.demo.dms

import org.fourthline.cling.support.model.container.Container

/**
 * @author Created by 25011 on 2018/8/2.
 * 资源存放工具类
 */
class ContentTree private constructor() {
    private var contentMap: HashMap<String, ContentItem>? = null
    var rootElement: ContentItem? = null

    companion object {
        val instance: ContentTree by lazy { ContentTree() }
        var ROOT_ID = "0"
        var VIDEO_ID = "1"
    }

    init {
        contentMap = HashMap()
        val container = Container()
        container.id = ROOT_ID
        container.title = "Media Server"
        container.creator = "media server"
        container.childCount = 0
        container.isRestricted = true
        rootElement = ContentItem(ROOT_ID, container)
        contentMap!![ROOT_ID] = rootElement!!
    }

    fun addContent(id: String, contentItem: ContentItem) {
        contentMap!![id] = contentItem
    }

    fun contain(id: String): Boolean {
        return contentMap!!.contains(id)
    }

    fun getContentItem(key: String): ContentItem? {
        return if (contain(key)) {
            contentMap!![key]
        } else {
            null
        }
    }

    fun getContentCount(): Int {
        return contentMap!!.size
    }
}