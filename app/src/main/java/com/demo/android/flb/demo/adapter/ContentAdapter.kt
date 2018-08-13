package com.demo.android.flb.demo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.demo.android.flb.demo.dms.ContentItem
import com.demo.android.flb.kotlin_upnp_demo.R

/**
 * @author Created by 25011 on 2018/7/30.
 * 内容展示适配
 */
class ContentAdapter(context: FragmentActivity) : RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {
    private var list: ArrayList<ContentItem>? = null
    private var post = 0
    var context: Context? = null

    init {
        this.context = context
    }

    fun setAdapterData(dataList: ArrayList<ContentItem>?) {
        list = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentAdapter.ContentViewHolder {
        return ContentAdapter.ContentViewHolder(View.inflate(context, R.layout.content_recycle_item, null))
    }

    override fun getItemCount(): Int {
        var count = 0
        val list1 = list
        if (list1 != null) count = list1.size
        return count
    }

    override fun onBindViewHolder(holder: ContentAdapter.ContentViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val bean = list?.get(position)
        holder.view!!.setOnClickListener {
            post = position
            notifyDataSetChanged()
            onclickDeviceItem?.setOnClickListener(position, bean!!)
        }
        val title: String
        title = if (bean!!.isItem) {
            "item:${bean.item!!.title}"
        } else {
            "contain:${bean.container!!.title}"
        }
        holder.tvName!!.text = title
    }


    class ContentViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView? = null
        var view: View? = null

        init {
            view = itemView
            tvName = itemView!!.findViewById(R.id.tv_content_name)
        }
    }

    var onclickDeviceItem: ContentAdapter.OnclickContentItem? = null

    interface OnclickContentItem {
        fun setOnClickListener(post: Int, contentItem: ContentItem)
    }
}