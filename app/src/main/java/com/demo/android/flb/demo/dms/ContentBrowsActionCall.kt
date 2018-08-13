package com.demo.android.flb.demo.dms

import com.demo.android.flb.demo.fragment.InitContentView
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.contentdirectory.callback.Browse
import org.fourthline.cling.support.model.BrowseFlag
import org.fourthline.cling.support.model.DIDLContent

/**
 * @author Created by 25011 on 2018/8/3.
 * 内容浏览服务回调
 */
class ContentBrowsActionCall constructor(view: InitContentView, service: Service<*,*>, containerId: String, flag: BrowseFlag) : Browse(service, containerId, flag) {
    var view: InitContentView? = null

    init {
        this.view = view
    }

    override fun updateStatus(status: Status?) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun received(actionInvocation: ActionInvocation<out Service<*, *>>?, didl: DIDLContent?) {
        val items = didl?.items
        val containers = didl?.containers

        val list = ArrayList<ContentItem>()
        if (items != null) {
            for (item in items) {
                list.add(ContentItem(item.title, item, ""))
            }
        }
        if (containers != null) {
            for (contain in containers) {
                list.add(ContentItem(contain.title, contain))
            }
        }
        view!!.refreshView(list)
        println("result:$list")
    }

    override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
        println("result: failure")
    }
}