package com.mike.cn.controltab.ui.activity

import android.os.Build
import android.view.View
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mike.cn.controltab.R
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.WifiUtils
import com.mike.cn.controltab.ui.adapters.WifiAdapter
import com.mike.cn.controltab.ui.base.BaseActivity

class WifiActivity : BaseActivity() {

    var rvDataView: RecyclerView? = null
    var openWifi: Switch? = null
    var ivBack: View? = null

    var wifiAdapter: WifiAdapter? = null
    var wifiUtils: WifiUtils? = null


    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_wifi)
    }

    /**
     * 初始化页面控件
     */
    override fun initView() {
        rvDataView = findViewById(R.id.rv_data)
        openWifi = findViewById(R.id.openWifi)
        ivBack = findViewById(R.id.iv_back)
        wifiAdapter = WifiAdapter();
        rvDataView?.layoutManager = GridLayoutManager(context, 1)
        rvDataView?.adapter = wifiAdapter

        wifiUtils = WifiUtils(this)

    }

    /**
     * 数据初始化
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun obtainData() {
        openWifi?.isChecked = wifiUtils?.isWifiEnabled == true
        if (openWifi?.isChecked == true) {
            getWifiList(false)
        }


    }

    /**
     * 事件初始化
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initEvent() {
        openWifi?.setOnCheckedChangeListener { compoundButton, b ->
            //是否打开
            if (b) {
                wifiUtils?.enableWifi()
                getWifiList(false)
            } else {
                wifiUtils?.disableWifi()
                getWifiList(true)
            }
        }
        ivBack?.setOnClickListener {
            onBackPressed()
        }
    }


    /**
     * 刷新获取 wifi 列表
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getWifiList(isClear: Boolean) {
        if (isClear) {
            wifiAdapter?.data?.clear()
        } else {
            val dataList = wifiUtils?.scanWifiNetworks()
            val newDataList = ArrayList<MenuInfoModel>()
            if (dataList != null) {
                for (t in dataList) {
                    val info = MenuInfoModel(0)
                    info.name = t.SSID
                    newDataList.add(info)
                }
            }
            wifiAdapter?.setList(newDataList)
        }
    }

}