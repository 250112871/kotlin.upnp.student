package com.demo.android.flb.demo.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.demo.android.flb.demo.bean.DeviceItem
import com.demo.android.flb.kotlin_upnp_demo.R

/**
 * @author Created by 25011 on 2018/7/30.
 * 设备展示Fragment适配
 */
class DeviceAdapter : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
    private var list: ArrayList<DeviceItem>? = null
    private var post = 0
    fun setAdapterData(dataList: ArrayList<DeviceItem>?) {
        list = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(View.inflate(parent.context, R.layout.device_recycle_item, null))
    }

    override fun getItemCount(): Int {
        var count = 0
        val list1 = list
        if (list1 != null) count = list1.size
        return count
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val bean = list?.get(position)


        holder.view?.setOnClickListener {
            post = position
            notifyDataSetChanged()
            onclickDeviceItem?.setOnClickListener(position, bean!!)
        }
        holder.tvName!!.text = bean!!.name
        if (position == post) {
            holder.icon!!.visibility = View.VISIBLE
        } else {
            holder.icon!!.visibility = View.GONE
        }
    }


    inner class DeviceViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView? = null
        var view: View? = null
        var icon: ImageView? = null

        init {
            view = itemView
            tvName = itemView!!.findViewById(R.id.tv_device_name)
            icon = itemView.findViewById(R.id.iv_arrow)
        }
    }

    var onclickDeviceItem: OnclickDeviceItem? = null

    interface OnclickDeviceItem {

        fun setOnClickListener(post: Int, device: DeviceItem)
    }
}