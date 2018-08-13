package com.demo.android.flb.demo.dms

import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService
import org.fourthline.cling.support.contentdirectory.DIDLParser
import org.fourthline.cling.support.model.BrowseFlag
import org.fourthline.cling.support.model.BrowseResult
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.SortCriterion

/**
 * @author Created by 25011 on 2018/8/2.
 * 内容浏览服务，通过请求参数返回对应资源信息
 */
class ContentDirectoryService : AbstractContentDirectoryService() {
    override fun browse(objectID: String?, browseFlag: BrowseFlag?, filter: String?, firstResult: Long, maxResults: Long, orderby: Array<out SortCriterion>?): BrowseResult {
        println("浏览服务 browseResult:$objectID")
        val content = DIDLContent()
        val parser = DIDLParser()
        if (objectID != null && ContentTree.instance.contain(objectID)) {
            val contentItem = ContentTree.instance.getContentItem(objectID)
            if (contentItem!!.isItem) {
                content.addItem(contentItem.item)
            } else {
                val containers = contentItem.container
                if (containers != null) {
                    for (con in containers.containers) {
                        content.addContainer(con)
                    }
                    for (item in containers.items) {
                        content.addItem(item)
                    }
                }
            }
        }
        val generate = parser.generate(content)
        return BrowseResult(generate, (content.containers.size + content.items.size).toLong(), 1)
    }
}