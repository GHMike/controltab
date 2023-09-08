package com.mike.cn.controltab.ui.activity

import android.net.wifi.ScanResult
import android.view.View
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mike.cn.controltab.R
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.ui.adapters.WifiAdapter
import com.mike.cn.controltab.ui.base.BaseActivity
import com.thanosfisherman.wifiutils.WifiUtils


class WifiActivity : BaseActivity() {

    var rvDataView: RecyclerView? = null
    var openWifi: Switch? = null
    var ivBack: View? = null
    var swipeRes: SwipeRefreshLayout? = null

    var wifiAdapter: WifiAdapter? = null


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
        swipeRes = findViewById(R.id.swipeRes)
        wifiAdapter = WifiAdapter();
        rvDataView?.layoutManager = GridLayoutManager(context, 1)
        rvDataView?.adapter = wifiAdapter


        swipeRes?.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        swipeRes?.setOnRefreshListener {
            swipeRes?.isRefreshing = true
            getWifiList(false)
        }

    }

    /**
     * 数据初始化
     */
    override fun obtainData() {
//        openWifi?.isChecked = WifiUtils.withContext(applicationContext).mPatternMatch
//        if (openWifi?.isChecked == true) {
        getWifiList(false)
//        }
    }


    /**
     * 事件初始化
     */
    override fun initEvent() {
        openWifi?.setOnCheckedChangeListener { compoundButton, b ->
            //是否打开
            if (b) {
                WifiUtils.withContext(applicationContext).enableWifi();
                getWifiList(false)
            } else {
                WifiUtils.withContext(applicationContext).disableWifi();
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
    private fun getWifiList(isClear: Boolean) {
        if (isClear) {
            wifiAdapter?.data?.clear()
        } else {
            swipeRes?.isRefreshing = true
            WifiUtils.withContext(applicationContext).scanWifi(this::getScanResults).start();
        }
    }

    //扫描结果回掉
    private fun getScanResults(results: List<ScanResult>) {
        swipeRes?.isRefreshing = false
        if (results.isEmpty()) {
            return
        }
        val newDataList = ArrayList<MenuInfoModel>()
        for (t in results) {
            val info = MenuInfoModel(0)
            info.name = t.SSID
            newDataList.add(info)
        }
        wifiAdapter?.setList(newDataList)
    }
}