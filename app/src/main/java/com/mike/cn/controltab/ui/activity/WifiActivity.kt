package com.mike.cn.controltab.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.provider.Settings
import android.view.*
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mike.cn.controltab.R
import com.mike.cn.controltab.tools.HideNavBarUtil
import com.mike.cn.controltab.tools.PermissionsChecker
import com.mike.cn.controltab.tools.WifiData
import com.mike.cn.controltab.tools.WifiHelper
import com.mike.cn.controltab.ui.adapters.WifiAdapter
import com.mike.cn.controltab.ui.base.BaseActivity


@SuppressLint("NotifyDataSetChanged,UseSwitchCompatOrMaterialCode")
class WifiActivity : BaseActivity() {

    private var rvDataView: RecyclerView? = null
    private var openWifi: Switch? = null
    private var ivBack: View? = null
    private var tvWifiState: TextView? = null
    var swipeRes: SwipeRefreshLayout? = null

    private var wifiAdapter: WifiAdapter? = null
    private var mPermissionsChecker: PermissionsChecker? = null
    private var wifiListBeanList: ArrayList<WifiData>? = arrayListOf()


    private var dialog: Dialog? = null
    private var inflate: View? = null
    private var mWifiHelp: WifiHelper? = null

    //定位权限,获取app内常用权限
    var permsLocation = arrayOf(
        "android.permission.ACCESS_WIFI_STATE",
        "android.permission.CHANGE_WIFI_STATE",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION"
    )
    private val RESULT_CODE_LOCATION = 0x001

    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_wifi)
        getPerMission()
        initWifi()
    }


    //获取权限
    private fun getPerMission() {
        mPermissionsChecker = PermissionsChecker(this)
        if (mPermissionsChecker!!.lacksPermissions(*permsLocation)) {
            ActivityCompat.requestPermissions(
                this,
                permsLocation,
                RESULT_CODE_LOCATION
            )
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
        rvDataView?.addItemDecoration(DividerItemDecoration(this, 1))
        rvDataView?.adapter = wifiAdapter
        mWifiHelp?.setWifiEnabled(true)
        val wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivity(wifiSettingsIntent)



        swipeRes?.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        swipeRes?.setOnRefreshListener {
            swipeRes?.isRefreshing = true
            mWifiHelp?.scan()
        }

    }

    /**
     * 数据初始化
     */
    override fun obtainData() {
    }


    /**
     * 事件初始化
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun initEvent() {
        ivBack?.setOnClickListener {
            onBackPressed()
        }

        //点击链接
        wifiAdapter?.setOnItemClickListener { _, _, i ->
            wifiAdapter?.getItem(i)?.ssid?.let {
                val item = wifiAdapter?.getItem(i)
                if (item != null) {
                    showCentreDialog(item)
                }
            }
        }
    }


    //扫描结果回掉
    private fun getScanResults(results: List<WifiData>) {
        swipeRes?.isRefreshing = false
        wifiAdapter?.setList(results)
    }


    //中间显示的dialog
    private fun showCentreDialog(wifiInfo: WifiData) {
        //自定义dialog显示布局
        inflate = LayoutInflater.from(this).inflate(R.layout.dialog_centre, null)
        //自定义dialog显示风格
        dialog = Dialog(this, R.style.DialogCentre)
        HideNavBarUtil.hideNavigation(dialog)
        //点击其他区域消失
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.setContentView(inflate!!)
        val window: Window = dialog!!.window!!
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = wlp
        dialog!!.show()
        val tvName: TextView = dialog!!.findViewById(R.id.tvName)
        val tvMargin: TextView = dialog!!.findViewById(R.id.tvMargin)
        val et_password: EditText = dialog!!.findViewById(R.id.et_password)
        tvName.text = "wifi：${wifiInfo.ssid}"
        tvMargin.setOnClickListener { //确定
            mWifiHelp?.connect(wifiInfo, et_password.text.toString())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 获取 WifiManager 实例
                val wifiManager = application.getSystemService(WIFI_SERVICE) as WifiManager

                // 创建 WifiNetworkSpecifier.Builder
                val builder = WifiNetworkSpecifier.Builder();
                // 设置 SSID 和密码
                builder.setSsid(wifiInfo.ssid);
                builder.setWpa2Passphrase(et_password.text.toString());
                // 构建 WifiNetworkSpecifier
                val wifiNetworkSpecifier = builder.build();
                // 创建 NetworkRequest.Builder
                val networkCallback = ConnectivityManager.NetworkCallback();
                // 请求连接到指定的 Wi-Fi 网络
                val connectivityManager: ConnectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;
                connectivityManager.requestNetwork(
                    NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .setNetworkSpecifier(wifiNetworkSpecifier)
                        .build(), networkCallback
                )
                // 创建 WifiNetworkSuggestion.Builder
                val builderSave = WifiNetworkSuggestion.Builder()
                builderSave.setSsid(wifiInfo.ssid);
                builderSave.setWpa2Passphrase(et_password.text.toString());
                // 构建 WifiNetworkSuggestion
                val wifiNetworkSuggestion: WifiNetworkSuggestion = builderSave.build()
                // 创建一个列表来保存要记住的 Wi-Fi 网络
                val suggestions: MutableList<WifiNetworkSuggestion> = ArrayList()
                suggestions.add(wifiNetworkSuggestion)
                // 提交 Wi-Fi 网络建议
                wifiManager.addNetworkSuggestions(suggestions)
                // 通过 networkCallback 监听网络连接状态
            }

            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    /**
     * 权限回调
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RESULT_CODE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "权限打开失败，请开启必要的权限", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun initWifi() {
        mWifiHelp = WifiHelper.Build(this)
            .setWifiStateChangedListener {
                when (it) {
                    //WiFi已关闭
                    WifiManager.WIFI_STATE_DISABLED -> {
                    }
                    //WiFi已开启
                    WifiManager.WIFI_STATE_ENABLED -> {

                    }
                    //WiFi状态未知
                    WifiManager.WIFI_STATE_UNKNOWN -> {
                    }
                }
            }
            .setNetworkStateChangedListener {
                wifiAdapter?.refreshConnectWifiStatus(
                    when (it) {
                        NetworkInfo.DetailedState.AUTHENTICATING -> "验证密码"
                        NetworkInfo.DetailedState.CONNECTING -> "正在连接"
                        NetworkInfo.DetailedState.CONNECTED -> ""
                        NetworkInfo.DetailedState.DISCONNECTING -> "正在断开"
                        NetworkInfo.DetailedState.DISCONNECTED -> ""
                        NetworkInfo.DetailedState.FAILED -> "连接失败"
                        else -> ""
                    }
                )
            }
            .setScanCallback {
                swipeRes?.isRefreshing = false
                if (it != null) {
                    getScanResults(it)
                }
            }
            .setAlreadyConnectionCallback {
                wifiAdapter?.refreshConnectWifiData(it)
            }
            .setErrorAuthenticating {
                //密码错误
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
            .build()
    }


    override fun onDestroy() {
        super.onDestroy()
        mWifiHelp?.destroy()
    }

    override fun onResume() {
        super.onResume()
        if (mWifiHelp!!.isWifiEnabled()) {
            mWifiHelp?.scan()
        }
    }
}