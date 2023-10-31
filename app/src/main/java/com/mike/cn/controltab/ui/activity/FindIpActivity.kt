package com.mike.cn.controltab.ui.activity

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig.PORT_NUM
import com.mike.cn.controltab.model.FindIpModel
import com.mike.cn.controltab.tools.ConfigHelper
import com.mike.cn.controltab.tools.UdpUtil
import com.mike.cn.controltab.ui.base.BaseActivity
import com.tencent.mmkv.MMKV


/**
 * 连接地址查找页面
 */
class FindIpActivity : BaseActivity(), View.OnClickListener {


    var dev_id: TextView? = null
    var tv_report: EditText? = null
    var et_Id: EditText? = null
    var etName: EditText? = null
    var et_password: EditText? = null
    var portNum: EditText? = null
    var gb_port: EditText? = null
    var gb_adds: EditText? = null
    var butSend: Button? = null

    var but2: Button? = null
    var but3: Button? = null
    var but4: Button? = null


    var ivBack: View? = null
    private val UDP_RCV_TAG = "udp_rcv_tag"
    private val ID_TAG = "id_TAG"
    private val WIFINAME_TAG = "wifiName_TAG"
    private val PASSWNAME_TAG = "passwName_TAG"
    private val PORTNAME_TAG = "portName_TAG"
    private val GB_TAG = "gb_TAG"
    private val GBPORT_TAG = "gbport_TAG"
    private val udpRcvStrBuf = StringBuffer("")

    private val mmkv: MMKV = MMKV.defaultMMKV()

    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_find_ip)
    }

    override fun initView() {
        dev_id = findViewById(R.id.dev_id)
        et_Id = findViewById(R.id.et_Id)
        tv_report = findViewById(R.id.tv_report)
        etName = findViewById(R.id.et_Name)
        et_password = findViewById(R.id.et_password)
        portNum = findViewById(R.id.port_Num)
        gb_port = findViewById(R.id.et_gbport)
        gb_adds = findViewById(R.id.et_gb)
        ivBack = findViewById(R.id.iv_back)
        butSend = findViewById(R.id.but_send)

        but2 = findViewById(R.id.but_2)
        but3 = findViewById(R.id.but_3)
        but4 = findViewById(R.id.but_4)
        ivBack?.setOnClickListener(this)
        but2?.setOnClickListener(this)
        but3?.setOnClickListener(this)
        but4?.setOnClickListener(this)
        butSend?.setOnClickListener(this)

    }

    override fun obtainData() {
        ConfigHelper().getEnCode()?.let { et_Id?.setText(it) }
        et_Id?.setText(mmkv.getString(ID_TAG, ConfigHelper().getEnCode()))
        etName?.setText(mmkv.getString(WIFINAME_TAG, ""))
        et_password?.setText(mmkv.getString(PASSWNAME_TAG, ""))
        portNum?.setText(mmkv.getString(PORTNAME_TAG, "9999"))

        gb_adds?.setText(mmkv.getString(GB_TAG, "192.168.0.255"))
        gb_port?.setText(mmkv.getString(GBPORT_TAG, "9999"))

    }


    override fun initEvent() {
        LiveEventBus.get(UDP_RCV_TAG, String::class.java).observe(this) { it ->
            tv_report?.setText(it) // 将光标移动到文本的最后
            tv_report?.setSelection(tv_report?.text.toString().length)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.but_send -> {
                if (etName?.text.toString().trim().isEmpty()) {
                    Toast.makeText(context, "请输入 wif i名称", Toast.LENGTH_SHORT).show()
                    return
                }
                if (et_password?.text.toString().trim().isEmpty()) {
                    Toast.makeText(context, "请输入 wifi 密码", Toast.LENGTH_SHORT).show()
                    return
                }
                if (portNum?.text.toString().trim().isEmpty()) {
                    Toast.makeText(context, "请输入端口号", Toast.LENGTH_SHORT).show()
                    return
                }
                sendJson(0)
            }
            R.id.but_2 -> {
                sendJson(1)
            }

            R.id.but_3 -> {
                sendJson(2)
            }
            R.id.but_4 -> {
                sendJson(3)
            }
            R.id.iv_back -> {
                onBackPressed()
            }
        }
    }


    //发送json请求
    private fun sendJson(type: Int) {
        var sendInfo: FindIpModel? = null
        when (type) {
            0 -> {
                sendInfo = FindIpModel(0)
                sendInfo.req = "set"
                sendInfo.id = et_Id?.text.toString()
                sendInfo.ssid = etName?.text.toString()
                sendInfo.pass = et_password?.text.toString()
                sendInfo.port = portNum?.text.toString()

                mmkv.putString(ID_TAG, sendInfo.id)
                mmkv.putString(WIFINAME_TAG, sendInfo.ssid)
                mmkv.putString(PASSWNAME_TAG, sendInfo.pass)
                mmkv.putString(PORTNAME_TAG, sendInfo.port)
            }
            1 -> {
                sendInfo = FindIpModel(0)
                sendInfo.req = "consta"
            }
            2 -> {
                sendInfo = FindIpModel(0)
                sendInfo.req = "bcast"
                sendInfo.id = et_Id?.text.toString()

            }
            3 -> {
                sendInfo = FindIpModel(0)
                sendInfo.req = "reset"
                sendInfo.id = et_Id?.text.toString()
            }
        }

        val sendJson = Gson().toJson(sendInfo)
        when (type) {
            0, 1 -> {
                UdpUtil.getInstance().sendUdpCommand(sendJson, false)
            }
            2, 3 -> {
                var portNum = 9999
                if (gb_port?.text.toString().isNotEmpty())
                    portNum = gb_port?.text.toString().toInt()
                UdpUtil.getInstance()
                    .sendUdpCommand(sendJson, false, gb_adds?.text.toString(), portNum)

                mmkv.putString(GB_TAG, gb_adds?.text.toString())
                mmkv.putString(GBPORT_TAG, portNum.toString())
            }
        }

        UdpUtil.getInstance().startListening {
            Log.e("udp返回", it)
            udpRcvStrBuf.append(it.toString())
            udpRcvStrBuf.append("\n")
            LiveEventBus.get<String>(UDP_RCV_TAG).post(udpRcvStrBuf.toString())
        }
    }

}