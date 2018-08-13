package com.demo.android.flb.demo.dms

import org.fourthline.cling.support.model.container.Container
import org.fourthline.cling.support.model.item.Item

/**
 * @author Created by 25011 on 2018/8/2.
 * 内容展示的Bean
 */
class ContentItem constructor(val id: String) {
    var container: Container? = null
    var item: Item? = null
    var isItem: Boolean = true
    var thumbPath: String = ""

    constructor(id: String, container: Container) : this(id) {
        this.container = container
        isItem = false
    }

    constructor(id: String, item: Item, thumbPath: String) : this(id) {
        this.item = item
        isItem = true
        this.thumbPath = thumbPath
    }

    override fun toString(): String {
        return "isItem:$isItem thumbPath:$thumbPath item:$item container:$container"
    }
}