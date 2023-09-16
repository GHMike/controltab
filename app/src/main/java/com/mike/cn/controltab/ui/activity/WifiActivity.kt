package com.mike.cn.controltab.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mike.cn.controltab.R
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.WifiUtils
import com.mike.cn.controltab.ui.adapters.WifiAdapter
import com.mike.cn.controltab.ui.base.BaseActivity


@SuppressLint("NotifyDataSetChanged,UseSwitchCompatOrMaterialCode")
class WifiActivity : BaseActivity() {

    var rvDataView: RecyclerView? = null
    var openWifi: Switch? = null
    var ivBack: View? = null
    var tvWifiState: TextView? = null
    var swipeRes: SwipeRefreshLayout? = null

    var wifiAdapter: WifiAdapter? = null
    private var mWifiManager: WifiManager? = null
    private var mScanResultList //wifi列表
            : List<ScanResult>? = null

    private var wifiListBeanList: ArrayList<MenuInfoModel>? = null


    private var dialog: Dialog? = null
    private var inflate: View? = null
    private var wifiReceiver: WifiBroadcastReceiver? = null

    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_wifi)
        if (mWifiManager == null) {
            mWifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        }
    }

    /**
     * 初始化页面控件
     */
    override fun initView() {
        rvDataView = findViewById(R.id.rv_data)
        openWifi = findViewById(R.id.openWifi)
        ivBack = findViewById(R.id.iv_back)
        swipeRes = findViewById(R.id.swipeRes)
        tvWifiState = findViewById(R.id.tv_wifiState)
        wifiAdapter = WifiAdapter();
        rvDataView?.layoutManager = LinearLayoutManager(context)
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
//                WifiUtils.withContext(applicationContext).enableWifi();
                getWifiList(false)
            } else {
//                WifiUtils.withContext(applicationContext).disableWifi();
                getWifiList(true)
            }
        }
        ivBack?.setOnClickListener {
            onBackPressed()
        }


        //点击链接
        wifiAdapter?.setOnItemClickListener { _, _, i ->
            wifiAdapter?.getItem(i)?.name?.let { showCentreDialog(it, i) };
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
            //开启wifi
            WifiUtils.openWifi(mWifiManager)
            //获取到wifi列表
            mScanResultList = WifiUtils.getWifiList(mWifiManager)
            for (i in mScanResultList!!.indices) {
                val wifiListBean = MenuInfoModel(0)
                wifiListBean.name = (mScanResultList!![i].SSID)
                wifiListBean.type = (WifiUtils.getEncrypt(mWifiManager, mScanResultList!![i]))
                wifiListBeanList?.add(wifiListBean)
            }

            if (wifiListBeanList?.size!! > 0) {
                wifiAdapter?.notifyDataSetChanged()
                Toast.makeText(this, "获取wifi列表成功", Toast.LENGTH_SHORT).show()
            } else {
                wifiAdapter?.notifyDataSetChanged()
                Toast.makeText(this, "wifi列表为空，请检查wifi页面是否有wifi存在", Toast.LENGTH_SHORT)
                    .show()
            }

//            WifiUtils.withContext(applicationContext).scanWifi(this::getScanResults).start();
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


    //监听wifi变化
    private fun registerReceiverWifi() {
        wifiReceiver = WifiBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION) //监听wifi是开关变化的状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION) //监听wifi连接状态
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) //监听wifi列表变化（开启一个热点或者关闭一个热点）
        registerReceiver(wifiReceiver, filter)
    }


    //中间显示的dialog
    fun showCentreDialog(wifiName: String, position: Int) {
        //自定义dialog显示布局
        inflate = LayoutInflater.from(this).inflate(R.layout.dialog_centre, null)
        //自定义dialog显示风格
        dialog = Dialog(this, R.style.DialogCentre)
        //点击其他区域消失
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.setContentView(inflate!!)
        val window: Window = dialog!!.window!!
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = wlp
        dialog!!.show()
        val tvName: TextView
        val tvMargin: TextView
        val et_password: EditText
        tvName = dialog!!.findViewById<TextView>(R.id.tvName)
        tvMargin = dialog!!.findViewById<TextView>(R.id.tvMargin)
        et_password = dialog!!.findViewById<EditText>(R.id.et_password)
        tvName.text = "wifi：$wifiName"
        tvMargin.setOnClickListener { //确定
            WifiUtils.disconnectNetwork(mWifiManager) //断开当前wifi
            val type: String =
                WifiUtils.getEncrypt(mWifiManager, mScanResultList!![position]) //获取加密方式
            Log.e("=====连接wifi:", "$wifiName；加密方式$type")
            WifiUtils.connectWifi(
                mWifiManager,
                wifiName,
                et_password.text.toString(),
                type
            ) //连接wifi
            dialog!!.dismiss()
        }
    }


    //监听wifi状态广播接收器
   private class WifiBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION == intent.action) {
                //wifi开关变化
                val state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)
                when (state) {
                    WifiManager.WIFI_STATE_DISABLED -> {
                        //wifi关闭
                        Log.e("=====", "已经关闭")
//                        tvWifiState.append("\n 打开变化：wifi已经关闭")
                    }
                    WifiManager.WIFI_STATE_DISABLING -> {

                        //wifi正在关闭
                        Log.e("=====", "正在关闭")
//                        tvWifiState.append("\n 打开变化：wifi正在关闭")
                    }
                    WifiManager.WIFI_STATE_ENABLED -> {

                        //wifi已经打开
                        Log.e("=====", "已经打开")
//                        tvWifiState.append("\n 打开变化：wifi已经打开")
                    }
                    WifiManager.WIFI_STATE_ENABLING -> {

                        //wifi正在打开
                        Log.e("=====", "正在打开")
//                        tvWifiState.append("\n 打开变化：wifi正在打开")
                    }
                    WifiManager.WIFI_STATE_UNKNOWN -> {

                        //未知
                        Log.e("=====", "未知状态")
//                        tvWifiState.append("\n 打开变化：wifi未知状态")
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION == intent.action) {
                //监听wifi连接状态
                val info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                Log.e("=====", "--NetworkInfo--" + info.toString())
                if (NetworkInfo.State.DISCONNECTED == info!!.state) { //wifi没连接上
                    Log.e("=====", "wifi没连接上")
//                    tvWifiState.append("\n 连接状态：wifi没连接上")
                } else if (NetworkInfo.State.CONNECTED == info.state) { //wifi连接上了
                    Log.e("=====", "wifi以连接")
//                    tvWifiState.append(
//                        "连接状态：wifi以连接，wifi名称：${WifiUtils.getWiFiName(mWifiManager)}"
//                    )
                } else if (NetworkInfo.State.CONNECTING == info.state) { //正在连接
                    Log.e("=====", "wifi正在连接")
//                    tvWifiState.append("\n 连接状态：wifi正在连接")
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == intent.action) {
                //监听wifi列表变化
                Log.e("=====", "wifi列表发生变化")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiverWifi() //监听wifi变化
    }

    override fun onPause() {
        super.onPause()
        //取消监听
        unregisterReceiver(wifiReceiver)
    }
}