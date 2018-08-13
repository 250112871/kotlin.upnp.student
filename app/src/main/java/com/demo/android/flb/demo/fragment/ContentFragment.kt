package com.demo.android.flb.demo.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.demo.android.flb.demo.activity.BaseApplication
import com.demo.android.flb.demo.activity.MainActivity
import com.demo.android.flb.demo.adapter.ContentAdapter
import com.demo.android.flb.demo.adapter.ContentAdapter.OnclickContentItem
import com.demo.android.flb.demo.dms.ContentBrowsActionCall
import com.demo.android.flb.demo.dms.ContentItem
import com.demo.android.flb.demo.utils.FileUtils
import com.demo.android.flb.demo.utils.KeyValueUtils
import com.demo.android.flb.demo.utils.KeyValueUtils.Companion.CONTROL_PLAY_UPDATE
import com.demo.android.flb.demo.utils.Lg
import com.demo.android.flb.kotlin_upnp_demo.R
import kotlinx.android.synthetic.main.fragment_content.*
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.support.contentdirectory.DIDLParser
import org.fourthline.cling.support.model.BrowseFlag
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.item.Item

/**
 * @author FLB
 * 根据选择的服务设备展示具体内容
 *
 */
class ContentFragment : Fragment(), InitContentView {
    private var adapter: ContentAdapter? = null
    private var currentService: Device<*, *, *>? = null
    private var currentDevice: Device<*, *, *>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        recycle_content.layoutManager = LinearLayoutManager(activity)
        adapter = ContentAdapter(activity!!)
        recycle_content.adapter = adapter
        adapter?.onclickDeviceItem = object : OnclickContentItem {
            override fun setOnClickListener(post: Int, contentItem: ContentItem) {
                if (contentItem.isItem) {
                    changeControl(contentItem.item)
                } else {
                    val id = contentItem.container!!.id
                    getServiceData(id, BrowseFlag.DIRECT_CHILDREN)
                }
            }
        }
        recycle_content.addItemDecoration(DividerItemDecoration(activity, LinearLayout.VERTICAL))
    }

    fun initData() {
        currentService = BaseApplication.instance!!.currentService
        currentDevice = BaseApplication.instance!!.currentDevice
        tv_content_title.text = currentService!!.details.friendlyName
        getServiceData("0", BrowseFlag.DIRECT_CHILDREN)
    }

    override fun refreshView(listData: ArrayList<ContentItem>) {
        Lg.i("数据发生变化更新页面")
        activity!!.runOnUiThread { adapter?.setAdapterData(listData) }
    }

    private fun getServiceData(resId: String, browseFlag: BrowseFlag) {
        val service = currentService!!.findService(UDAServiceType("ContentDirectory"))
        BaseApplication.instance!!.androidService!!.controlPoint.execute(ContentBrowsActionCall(this, service, resId, browseFlag))
    }

    private fun changeControl(item: Item?) {
        if (item != null) {
            val value = item.firstResource.value
            val fileSuffix = FileUtils.getFileSuffix(value)
            (activity as MainActivity).setTae(2)
            val addItem = DIDLContent().addItem(item)
            val generate = DIDLParser().generate(addItem)

            val intent = Intent(CONTROL_PLAY_UPDATE)
            intent.putExtra(KeyValueUtils.PATH, item.firstResource.value)
            intent.putExtra(KeyValueUtils.SUFFIX, fileSuffix)
            intent.putExtra(KeyValueUtils.META_DATA, generate)
            activity?.sendBroadcast(intent)
        }
    }
}

interface InitContentView {
    fun refreshView(listData: ArrayList<ContentItem>)
}
