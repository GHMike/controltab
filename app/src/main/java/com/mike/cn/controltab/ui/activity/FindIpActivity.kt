package com.mike.cn.controltab.ui.activity

import android.os.Handler
import android.os.Looper
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
    var tv_report: TextView? = null
    var et_Id: EditText? = null
    var etName: EditText? = null
    var et_password: EditText? = null
    var portNum: EditText? = null
    var butSend: Button? = null
    val port = MMKV.defaultMMKV()
    var ivBack: View? = null
    private val UDP_RCV_TAG = "udp_rcv_tag"
    private val udpRcvStrBuf = StringBuffer("")

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
        ivBack = findViewById(R.id.iv_back)
        butSend = findViewById(R.id.but_send)
        ivBack?.setOnClickListener(this)
        butSend?.setOnClickListener(this)
    }

    override fun obtainData() {
        portNum?.setText(port.getInt(PORT_NUM, 9999).toString())
        ConfigHelper().getEnCode()?.let { et_Id?.setText(it) }

    }


    override fun initEvent() {
        LiveEventBus.get(UDP_RCV_TAG, String::class.java).observe(this) { it ->
            tv_report?.text = it
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
                sendJson()
            }
            R.id.iv_back -> {
                onBackPressed()
            }
        }
    }


    //发送json请求
    private fun sendJson() {
        val sendInfo = FindIpModel(0)
        sendInfo.id = et_Id?.text.toString()
        sendInfo.ssid = etName?.text.toString()
        sendInfo.pass = et_password?.text.toString()
        sendInfo.port = portNum?.text.toString()
        sendInfo.req = "set"

        val sendJson = Gson().toJson(sendInfo)
        UdpUtil.getInstance().sendUdpCommand(sendJson)
        UdpUtil.getInstance().startListening {
            Log.e("udp返回", it)
            udpRcvStrBuf.append(it.toString())
            udpRcvStrBuf.append("\n")
            LiveEventBus.get<String>(UDP_RCV_TAG).post(udpRcvStrBuf.toString())
        }
    }

}