@file:OptIn(DelicateCoroutinesApi::class)

package com.mike.cn.controltab.tools

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.net.wifi.*
import android.os.Parcelable
import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlinx.coroutines.*


class WifiHelper {
    private lateinit var mWifiManager: WifiManager
    private var mBuild: Build? = null
    protected var mWiFiChangeReceiver: WiFiChangeReceiver? = WiFiChangeReceiver()

    private fun init(build: Build) {
        mWifiManager =
            build.mContext?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mBuild = build
        build.mContext?.let {
            registerReceiver(it)
        }
    }

    fun destroy() {
        unregisterReceiver()
        mBuild?.mContext = null
        mBuild?.mWifiStateChangedListener = null
        mBuild?.mNetworkStateChangedListener = null
        mBuild?.mScanCallback = null
        mBuild?.mAlreadyConnectionCallback = null
        mBuild?.mErrorAuthenticating = null
        mBuild = null
    }

    fun isWifiEnabled(): Boolean {
        return mWifiManager.isWifiEnabled
    }

    fun setWifiEnabled(isEnabled: Boolean) = mWifiManager.setWifiEnabled(isEnabled)


    /**
     * 搜索WiFi
     */
    fun scan() {
        mWifiManager.isScanAlwaysAvailable
        mWifiManager.startScan()
    }

    /**
     * 刷新已经连接的WiFi
     */
    fun refreshConnectWifiData() {
        GlobalScope.launch(Dispatchers.Main) {
            val connectionWifi = withContext(Dispatchers.IO) { getConnectionWifi() }
            mBuild?.mAlreadyConnectionCallback?.invoke(connectionWifi)
        }
    }

    fun connect(wifiData: WifiData) {
        connect(wifiData, "")
    }

    @SuppressLint("MissingPermission")
    fun connect(wifiData: WifiData, password: String) {
        val configurationList = mWifiManager.configuredNetworks //已经保存密码的WiFi
        val isSave = configurationList.any {
            removeQuotationMarks(it.SSID) == wifiData.ssid
        }
        if (isSave) {
            //如果是已经保存密码的的WiFi就重新连接
            mWifiManager.enableNetwork(wifiData.netId, true)
        } else {
            val wifiConfiguration = WifiConfiguration()
            //清除一些默认wifi的配置
            wifiConfiguration.allowedAuthAlgorithms.clear()
            wifiConfiguration.allowedGroupCiphers.clear()
            wifiConfiguration.allowedKeyManagement.clear()
            wifiConfiguration.allowedPairwiseCiphers.clear()
            wifiConfiguration.allowedProtocols.clear()
            wifiConfiguration.SSID = addQuotationMarks(wifiData.ssid)
            when (wifiData.capabilities) {
                WiFiPwdType.ESS -> {
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                }
                WiFiPwdType.WAP -> {
                    wifiConfiguration.preSharedKey = addQuotationMarks(password)
                    wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                    wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                    wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                    wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                    wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                    wifiConfiguration.status = WifiConfiguration.Status.ENABLED
                }
                WiFiPwdType.WEP -> {
                    wifiConfiguration.wepKeys[0] = addQuotationMarks(password)
                    wifiConfiguration.wepTxKeyIndex = 0;
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                }
                WiFiPwdType.WPA2_EAP -> {
                    //不支持企业WiFi密码
                }
            }
            val netId = mWifiManager.addNetwork(wifiConfiguration)
            mWifiManager.enableNetwork(netId, true)
        }
    }

    /**
     * 断开当前连接的网络
     */
    fun disconnect() {
        GlobalScope.launch(Dispatchers.IO) {
            val wifiData = getConnectionWifi() ?: return@launch
            mWifiManager.disconnect()
            mWifiManager.disableNetwork(wifiData.netId)
            mWifiManager.saveConfiguration()
            //自动重连其他可用网络
            mWifiManager.reconnect()
        }
    }

    /**
     * 更换密码WiFi
     */
    @SuppressLint("MissingPermission")
    fun changeWifiPwd(wifiData: WifiData, password: String) {
        val wifiConfigurationList = mWifiManager.configuredNetworks
        for (item in wifiConfigurationList) {
            if (item.SSID == null) {
                continue
            }
            if (item.SSID == addQuotationMarks(wifiData.ssid)) {
                item.preSharedKey = "\"" + password + "\""
                mWifiManager.disconnect()
                val id = mWifiManager.updateNetwork(item)
                if (id == -1) { //id如果等于 -1 就说明更新失败了
                    return
                }
                mWifiManager.enableNetwork(id, true) //启用连接WiFi
                mWifiManager.reconnect()
            }
        }
    }

    /**
     * 移除保存的WiFi
     *
     * @param ssid
     */
    @SuppressLint("MissingPermission")
    fun removeSaveWifi(wifiData: WifiData) {
        val wifiConfigurationList = mWifiManager.configuredNetworks
        for (item in wifiConfigurationList) {
            if (item.SSID == addQuotationMarks(wifiData.ssid)) {
                mWifiManager.disconnect()
                mWifiManager.removeNetwork(item.networkId)
                mWifiManager.saveConfiguration()
                mWifiManager.reconnect()
            }
        }
    }

    private fun registerReceiver(context: Context) {
        val intentFilter = IntentFilter()
        //当前连接WiFi状态的变化,这个监听是指当前已经连接WiFi的断开与重连的状态
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        //WiFi开关状态
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        //信号变化
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        //搜索Wifi扫描已完成，并且结果可用
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        //表明与接入点建立连接的状态已经改变。请求者状态是特定于 Wi-Fi 的
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        context.registerReceiver(mWiFiChangeReceiver, intentFilter)
    }

    private fun unregisterReceiver() {
        mBuild?.mContext?.unregisterReceiver(mWiFiChangeReceiver)
    }

    /**
     *  WiFi开关状态
     *  WifiManager.WIFI_STATE_DISABLED  //WiFi已关闭
     *  WifiManager.WIFI_STATE_DISABLING //WiFi关闭中
     *  WifiManager.WIFI_STATE_ENABLED //WiFi已开启
     *  WifiManager.WIFI_STATE_ENABLING //WiFi开启中
     *  WifiManager.WIFI_STATE_UNKNOWN //WiFi状态未知
     */
    private fun getWifiState() = mWifiManager.wifiState

    @SuppressLint("MissingPermission")
    private suspend fun getScanDevice(): List<WifiData> {
        val list: List<ScanResult> = mWifiManager.scanResults //获取WiFi列表
        val configurationList = mWifiManager.configuredNetworks //已经保存密码的WiFi
        val connectionWifi = getConnectionWifi()
        val wifiList = mutableListOf<WifiData>()
        for (scanResult in list) {
            if (scanResult.SSID.isNullOrEmpty()) {
                continue
            }
            if (scanResult.SSID == connectionWifi?.ssid) {
                continue
            }
            val isRepeat = wifiList.any { it.ssid == scanResult.SSID }
            if (!isRepeat) {
                val configuration =
                    configurationList.find { removeQuotationMarks(it.SSID) == scanResult.SSID }
                val netId = configuration?.networkId ?: 0
                val wifiData = WifiData(
                    netId,
                    scanResult.SSID,
                    formatLevel(scanResult.level),
                    scanResult.BSSID,
                    "",
                    securityType(scanResult.capabilities), configuration != null, 0
                )
                wifiList.add(wifiData)
            }
        }
        //根据是否已经保存密码与信号强度降序排序
        wifiList.sortWith(kotlin.Comparator { u1, u2 ->
            if (u1.isSavePwd) {
                u2.isSavePwd.compareTo(u1.isSavePwd)
            } else {
                u2.rssi.compareTo(u1.rssi)
            }
        })
        return wifiList
    }

    /**
     * 获取当前连接的WiFi网络
     */
    private suspend fun getConnectionWifi(): WifiData? {
        val wifiInfo = mWifiManager.connectionInfo ?: return null
        if (wifiInfo.ssid.contains("unknown ssid")) {
            //在频繁的切换WiFi与重连WiFi底层会返回 unknown ssid, 这里将其排除掉
            return null
        }
        return WifiData(
            wifiInfo.networkId,
            removeQuotationMarks(wifiInfo.ssid),
            formatLevel(wifiInfo.rssi),
            wifiInfo.bssid,
            getStringId(wifiInfo.ipAddress),
            WiFiPwdType.ESS,
            true, 0
        )
    }

    /**
     * 刷新wifi的全部数据（搜索的WiFi与当前连接WiFi数据）
     */
    private fun refreshWifiData() {
        GlobalScope.launch(Dispatchers.IO) {
            val list = getScanDevice()
            val connectionWifi = getConnectionWifi()
            withContext(Dispatchers.Main) {
                mBuild?.mScanCallback?.invoke(list)
                mBuild?.mAlreadyConnectionCallback?.invoke(connectionWifi)
            }
        }
    }

    /**
     * 移除引号
     *
     * @param content
     * @return
     */
    private fun removeQuotationMarks(content: String): String {
        return content.substring(1, content.length - 1)
    }

    /**
     * 添加引号
     *
     * @param content
     * @return
     */
    private fun addQuotationMarks(content: String): String {
        return "\"" + content + "\""
    }

    /**
     * 格式化信号
     *
     * WifiInfo.MIN_RSSI = -126;
     * WifiInfo.MAX_RSSI = 200;
     *
     * Quality     Excellent           Good            Fair            Poor
     * dBm        -30 ～ -61        -63 ～ -73     -75 ～ -85        -87 ～ -97
     *
     * @param rssi
     * @return
     */
    private fun formatLevel(rssi: Int): Int {
        return if (rssi < -97) {
            0
        } else if (rssi < -87) {
            1
        } else if (rssi < -75) {
            2
        } else if (rssi < -63) {
            3
        } else {
            4
        }
    }

    /**
     * 将idAddress转化成string类型的Id字符串
     *
     * @param idString
     * @return
     */
    private fun getStringId(idString: Int): String {
        val sb = StringBuffer()
        var b = idString shr 0 and 0xff
        sb.append("$b.")
        b = idString shr 8 and 0xff
        sb.append("$b.")
        b = idString shr 16 and 0xff
        sb.append("$b.")
        b = idString shr 24 and 0xff
        sb.append(b)
        return sb.toString()
    }

    /**
     *     ESS = 开放网络，不加密，无需密码
     *     WEP = 旧的加密方式，不推荐使用，仅需密码
     *     WAP  = 最常见的加密方式，仅需密码
     *     WPA2-EAP  = 企业加密方式，ID+密码验证
     */
    private fun securityType(capabilities: String?): WiFiPwdType {
        if (capabilities == null || capabilities.isEmpty()) {
            return WiFiPwdType.ESS
        }
        // 如果包含WAP-PSK的话，则为WAP加密方式
        if (capabilities.contains("WPA-PSK") || capabilities.contains("WPA2-PSK")) {
            return WiFiPwdType.WAP
        } else if (capabilities.contains("WPA2-EAP")) {
            return WiFiPwdType.WPA2_EAP
        } else if (capabilities.contains("WEP")) {
            return WiFiPwdType.WEP
        } else if (capabilities.contains("ESS")) {
            // 如果是ESS则没有密码
            return WiFiPwdType.ESS
        }
        return WiFiPwdType.ESS
    }

    class Build(context: Context) {
        internal var mScanCallback: ((List<WifiData>?) -> Unit)? = null
        internal var mAlreadyConnectionCallback: ((WifiData?) -> Unit)? = null
        internal var mWifiStateChangedListener: ((Int) -> Unit)? = null
        internal var mNetworkStateChangedListener: ((NetworkInfo.DetailedState) -> Unit)? = null
        internal var mErrorAuthenticating: ((String) -> Unit)? = null
        internal var mContext: Context? = context

        /**
         * 设置WiFi扫描回调
         */
        fun setScanCallback(callback: ((List<WifiData>?) -> Unit)): Build {
            mScanCallback = callback
            return this
        }

        /**
         * 设置当前正在连接的WiFi回调
         */
        fun setAlreadyConnectionCallback(callback: (WifiData?) -> Unit): Build {
            mAlreadyConnectionCallback = callback
            return this
        }

        /**
         * 设置连接时密码错误回调
         */
        fun setErrorAuthenticating(callback: (String) -> Unit): Build {
            mErrorAuthenticating = callback
            return this
        }

        /**
         * WiFi开关状态监听
         *  WifiManager.WIFI_STATE_DISABLED  //WiFi已关闭
         *  WifiManager.WIFI_STATE_DISABLING //WiFi关闭中
         *  WifiManager.WIFI_STATE_ENABLED //WiFi已开启
         *  WifiManager.WIFI_STATE_ENABLING //WiFi开启中
         *  WifiManager.WIFI_STATE_UNKNOWN //WiFi状态未知
         */
        fun setWifiStateChangedListener(wifiStateChangedListener: (Int) -> Unit): Build {
            mWifiStateChangedListener = wifiStateChangedListener
            return this
        }

        /**
         * 当前连接WiFi网络状态的变化,这个监听是指当前已经连接WiFi的断开与重连的状态
         * NetworkInfo.DetailedState.CONNECTED              //已经连接
         * NetworkInfo.DetailedState.DISCONNECTED           //已经断开
         * NetworkInfo.DetailedState.IDLE                   //空闲中
         * NetworkInfo.DetailedState.AUTHENTICATING         //认证中
         * NetworkInfo.DetailedState.BLOCKED                //认证失败
         * NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK //连接检查
         */
        fun setNetworkStateChangedListener(networkStateChangedListener: (NetworkInfo.DetailedState) -> Unit): Build {
            mNetworkStateChangedListener = networkStateChangedListener
            return this
        }

        fun build(): WifiHelper {
            val wifiHelp = WifiHelper()
            wifiHelp.init(this)
            return wifiHelp
        }
    }

    /**
     * WiFi监听
     */
    protected inner class WiFiChangeReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            //WiFi开关状态
            if (intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                val switchState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)//得到WiFi开关状态值
                mBuild?.mWifiStateChangedListener?.invoke(switchState)
            }
            //当前连接WiFi状态的变化,这个监听是指当前已经连接WiFi的断开与重连的状态
            if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                //得到信息包
                val parcelableExtra: Parcelable? =
                    intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)
                val networkInfo: NetworkInfo = parcelableExtra as NetworkInfo
                mBuild?.mNetworkStateChangedListener?.invoke(networkInfo.detailedState)
                //在每次连接成功or连接失败后更新一次数据
                if (networkInfo.detailedState == NetworkInfo.DetailedState.CONNECTED
                    || networkInfo.detailedState == NetworkInfo.DetailedState.DISCONNECTED
                ) {
                    refreshWifiData()
                }
            }
            //信号变化
            if (intent.action == WifiManager.RSSI_CHANGED_ACTION) {
                refreshConnectWifiData()
            }
            //搜索完成
            if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                refreshWifiData()
            }
            //请求变化
            if (intent.action == WifiManager.SUPPLICANT_STATE_CHANGED_ACTION) {
                val linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0)
                if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                    mBuild?.mErrorAuthenticating?.invoke("密码错误")
                }
            }
        }
    }

}

/**
 * wifi数据
 */
data class WifiData(
    val netId: Int,                 //WiFi网络id，只有已经保存密码的WiFi才有，用于重连网络
    val ssid: String,               //WiFi名称
    val rssi: Int,                  //WiFi信号强度 0到4  0信号最差 4信号最好
    val bssid: String?,             //WiFi地址
    val ipAddress: String?,         //ip地址(连接WiFi后才会有)
    val capabilities: WiFiPwdType,  //WiFi加密方式
    val isSavePwd: Boolean, //是否保存密码
    override val itemType: Int,
) : MultiItemEntity

enum class WiFiPwdType {
    //加密方式
    /**
     *  ESS = 开放网络，不加密，无需密码
     */
    ESS,

    /**
     * WEP = 旧的加密方式，不推荐使用，仅需密码
     */
    WEP,

    /**
     * WAP  = 最常见的加密方式，仅需密码
     */
    WAP,

    /**
     * WPA2-EAP  = 企业加密方式，ID+密码验证
     */
    WPA2_EAP,
}