package com.demo.android.flb.demo.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.demo.android.flb.demo.activity.BaseApplication
import com.demo.android.flb.demo.activity.MainActivity
import com.demo.android.flb.demo.adapter.DeviceAdapter
import com.demo.android.flb.demo.bean.DeviceItem
import com.demo.android.flb.demo.dmr.MediaRange
import com.demo.android.flb.demo.dms.MediaServer
import com.demo.android.flb.demo.utils.Lg
import com.demo.android.flb.kotlin_upnp_demo.R
import kotlinx.android.synthetic.main.fragment_device.*
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.registry.DefaultRegistryListener
import org.fourthline.cling.registry.Registry

/**
 * @author FLB
 * 设备管理Fragment
 * 1开启共享服务
 * 2监听服务设备增加
 * 3监听媒体设备增加
 *
 */
class DeviceFragment : Fragment() {

    private val deviceAdapter: DeviceAdapter by lazy { DeviceAdapter() }
    private val serviceAdapter: DeviceAdapter by lazy { DeviceAdapter() }
    private val deviceList: ArrayList<DeviceItem> by lazy { ArrayList<DeviceItem>() }
    private val serviceList: ArrayList<DeviceItem> by lazy { ArrayList<DeviceItem>() }
    private var androidService: AndroidUpnpService? = null
    private var serviceConnect = DeviceServiceConnect()

    init {
        deviceAdapter.onclickDeviceItem = object : DeviceAdapter.OnclickDeviceItem {
            override fun setOnClickListener(post: Int, device: DeviceItem) {
                BaseApplication.instance!!.currentDevice = if (device.isRemoteDevice) {
                    device.remoteDevice
                } else {
                    device.localDevice
                }
                (activity as MainActivity).initData()
            }
        }
        serviceAdapter.onclickDeviceItem = object : DeviceAdapter.OnclickDeviceItem {
            override fun setOnClickListener(post: Int, device: DeviceItem) {
                BaseApplication.instance!!.currentService = if (device.isRemoteDevice) {
                    device.remoteDevice
                } else {
                    device.localDevice
                }
                (activity as MainActivity).initData()
            }
        }
    }

    val registryListener = object : DefaultRegistryListener() {
        override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice?) {
            val type = device!!.type.type
            Lg.i("发现新设备 type:$type  friendName: ${device.details.friendlyName}")
            val deviceItem = DeviceItem(device.details.friendlyName, device)
            when (type) {
                MediaServer.TYPE -> {
                    if (serviceList.contains(deviceItem)) {
                        Lg.i("媒体服务设备已经存在清除")
                        serviceList.remove(deviceItem)
                    }
                    serviceList.add(deviceItem)
                    Lg.i("添加媒体服务设备")
                    activity?.runOnUiThread { serviceAdapter.notifyDataSetChanged() }
                }
                MediaRange.TYPE -> {
                    if (deviceList.contains(deviceItem)) {
                        Lg.i("媒体渲染设备已经存在清除")
                        deviceList.remove(deviceItem)
                    }
                    deviceList.add(deviceItem)
                    Lg.i("媒体渲染设备添加")
                    activity?.runOnUiThread { deviceAdapter.notifyDataSetChanged() }
                }
            }
        }

        override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice?) {
            val type = device!!.type.type
            Lg.i("远程设备消失 type:$type friendlyName:${device.details.friendlyName}")
            val deviceItem = DeviceItem(device.details.friendlyName, device)
            when (type) {
                MediaServer.TYPE -> {
                    if (serviceList.contains(deviceItem)) {
                        serviceList.remove(deviceItem)
                        Lg.i("删除媒体服务设备")
                        activity?.runOnUiThread { serviceAdapter.notifyDataSetChanged() }
                    }
                }
                MediaRange.TYPE -> {
                    if (deviceList.contains(deviceItem)) {
                        deviceList.remove(deviceItem)
                        Lg.i("删除媒体渲染设备")
                        activity?.runOnUiThread { deviceAdapter.notifyDataSetChanged() }
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_device, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        recycle_device.layoutManager = LinearLayoutManager(activity)
        recycle_device.adapter = deviceAdapter
        deviceAdapter.setAdapterData(deviceList)
        recycle_device.addItemDecoration(DividerItemDecoration(activity, LinearLayout.VERTICAL))

        recycle_service.layoutManager = LinearLayoutManager(activity)
        recycle_service.adapter = serviceAdapter
        serviceAdapter.setAdapterData(serviceList)
        recycle_service.addItemDecoration(DividerItemDecoration(activity, LinearLayout.VERTICAL))
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context?.bindService(Intent(context, AndroidUpnpServiceImpl::class.java), serviceConnect, Context.BIND_AUTO_CREATE)
    }

    override fun onDetach() {
        super.onDetach()
        context?.unbindService(serviceConnect)
    }

    inner class DeviceServiceConnect : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            androidService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            androidService = service as AndroidUpnpService
            BaseApplication.instance!!.androidService = androidService
            androidService!!.registry.addListener(registryListener)

            val mediaServerDevice = MediaServer().localDevice
            if (mediaServerDevice != null) {
                serviceList.add(DeviceItem(mediaServerDevice.details.friendlyName, mediaServerDevice))
                serviceAdapter.notifyDataSetChanged()
                androidService!!.registry.addDevice(mediaServerDevice)
                BaseApplication.instance!!.currentService = mediaServerDevice
                Lg.i("本地媒体服务设备增加")
            }

            val mediaRange = MediaRange(activity!!, 1).device
            if (mediaRange != null) {
                deviceList.add(DeviceItem(mediaRange.details.friendlyName, mediaRange))
                deviceAdapter.notifyDataSetChanged()
                androidService!!.registry.addDevice(mediaRange)
                BaseApplication.instance!!.currentDevice = mediaRange
                Lg.i("本地媒体渲染设备增加")
            }

            androidService!!.controlPoint.search()
            Lg.i("开启服务的搜索功能")
            (activity as MainActivity).initData()
        }
    }
}
