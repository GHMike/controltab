package com.mike.cn.controltab

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.jeremyliao.liveeventbus.LiveEventBus
import com.mike.cn.controltab.tools.UDPClient
import com.mike.cn.controltab.tools.UDPClient.ERROR_TAG
import com.mike.cn.controltab.tools.UDPClient.MES_TAG
import com.mike.cn.controltab.ui.activity.PortSetActivity
import com.mike.cn.controltab.ui.base.BaseActivity
import com.tencent.mmkv.MMKV
import java.util.concurrent.Executors

class MainActivity : BaseActivity(), View.OnClickListener {

    var con: TextView? = null
    var button: Button? = null
    var but_connect: Button? = null
    var but_connect_off: Button? = null
    var but_setting: Button? = null
    var edit_input: EditText? = null
    var client: UDPClient? = null


    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_main)
    }

    override fun initView() {
        con = findViewById(R.id.con)
        button = findViewById(R.id.button)
        but_connect = findViewById(R.id.but_connect)
        but_connect_off = findViewById(R.id.but_connect_off)
        but_setting = findViewById(R.id.but_setting)
        edit_input = findViewById(R.id.edit_input)
        button?.setOnClickListener(this)
        but_connect?.setOnClickListener(this)
        but_connect_off?.setOnClickListener(this)
        but_setting?.setOnClickListener(this)
    }

    override fun obtainData() {
        LiveEventBus.get(MES_TAG, String::class.java).observe(this) {
            con?.text = it
        }
        LiveEventBus.get(ERROR_TAG, String::class.java).observe(this) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun initEvent() {
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                val thread = Thread {
                    if (client != null && client!!.isUdpLife) {
                        client?.send(edit_input?.text.toString())

                    }
                }
                thread.start()

            }
            R.id.but_setting -> {
                val intent = Intent(context, PortSetActivity::class.java)
                startActivity(intent)
            }
            R.id.but_connect -> {

                val port = MMKV.defaultMMKV()
                if (port.getString("ipadds", "") == null || port.getString(
                        "ipadds",
                        ""
                    )!!.isEmpty()
                ) {
                    Toast.makeText(context, "没有设置访问地址，请设置访问地址", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, PortSetActivity::class.java)
                    startActivity(intent)
                    return
                }
                if (client == null || !client!!.isUdpLife) {
                    //建立线程池
                    val exec = Executors.newCachedThreadPool()
                    client = UDPClient()
                    client?.connect()
                    exec.execute(client)
                }

            }
            R.id.but_connect_off -> {
                if (client != null && client!!.isUdpLife) {
                    client?.isUdpLife = false
                    client?.closeConnect()
                }
            }
        }
    }
}