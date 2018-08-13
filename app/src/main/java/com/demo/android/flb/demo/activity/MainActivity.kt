package com.demo.android.flb.demo.activity

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import com.demo.android.flb.demo.adapter.DeviceAdapter
import com.demo.android.flb.demo.fragment.ContentFragment
import com.demo.android.flb.demo.fragment.ControlFragment
import com.demo.android.flb.demo.fragment.DeviceFragment
import com.demo.android.flb.demo.fragment.SettingFragment
import com.demo.android.flb.demo.utils.Lg
import com.demo.android.flb.kotlin_upnp_demo.R
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import java.util.*
import java.util.Collections.addAll

/**
 * @author FLB
 * 主页面包括设备，内容，控制 设置（未实现）
 */
class MainActivity : FragmentActivity() {
    private var contentFragment = ContentFragment()
    private var controlFragment = ControlFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private var adapter: RecyclerView.Adapter<*>? = null

    private var commonTabLayout: CommonTabLayout? = null

    private fun initView() {
        commonTabLayout = this.findViewById(R.id.common_table)
        val device = HomeCustomTabEntity("设备", R.mipmap.menu_icon_0_pressed, R.mipmap.menu_icon_0_normal)
        val content = HomeCustomTabEntity("内容", R.mipmap.menu_icon_1_pressed, R.mipmap.menu_icon_1_normal)
        val control = HomeCustomTabEntity("控制台", R.mipmap.menu_icon_2_pressed, R.mipmap.menu_icon_2_normal)
        val set = HomeCustomTabEntity("设置", R.mipmap.menu_icon_3_pressed, R.mipmap.menu_icon_3_normal)
        val customList = ArrayList<CustomTabEntity>()
        addAll(customList, device, content, control, set)
        commonTabLayout?.setTabData(customList, this, R.id.fl_fragment_content, arrayListOf(DeviceFragment(), contentFragment, controlFragment, SettingFragment()))
        adapter = DeviceAdapter()
    }

    fun initData() {
        Lg.i("选择设备发生变化，重新初始化数据")
        contentFragment.initData()
        controlFragment.initData()
    }

    fun setTae(post: Int) {
        commonTabLayout?.currentTab = post
    }

    class HomeCustomTabEntity constructor(name: String, @DrawableRes select: Int, @DrawableRes unSelect: Int) : CustomTabEntity {
        private var titleSelect = select
        private var titleName = name
        private var titleUnSelect = unSelect
        override fun getTabUnselectedIcon(): Int {
            return titleUnSelect
        }

        override fun getTabSelectedIcon(): Int {
            return titleSelect
        }

        override fun getTabTitle(): String {
            return titleName
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        System.exit(0)
    }
}
