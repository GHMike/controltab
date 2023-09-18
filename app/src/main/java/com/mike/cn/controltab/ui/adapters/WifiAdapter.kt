package com.mike.cn.controltab.ui.adapters

import android.util.Log
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mike.cn.controltab.R
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.WifiData


/**
 * wifi适配器
 */
class WifiAdapter() : BaseMultiItemQuickAdapter<WifiData, BaseViewHolder>() {

    init {
        addItemType(0, R.layout.item_wifi)
    }

    override fun convert(holder: BaseViewHolder, item: WifiData) {

        holder.setText(R.id.tv_Name, item.ssid ?: "")
        holder.setText(R.id.tv_type, item.capabilities.toString() ?: "")
        if (item.ipAddress != null && item.ipAddress.isNotEmpty()) {
            holder.setText(R.id.tv_con, "已连接")
        } else {
            holder.setText(R.id.tv_con, "")
        }
    }

    /**
     *刷新数据
     */
    fun refreshConnectWifiData(it: WifiData?) {
        Log.e("refreshConnectWifiData", it.toString())
        if (it != null) {
            this.data.add(0, it)
        }
    }

    /**
     *更新现在连接的状态
     */
    fun refreshConnectWifiStatus(any: Any) {

    }


}