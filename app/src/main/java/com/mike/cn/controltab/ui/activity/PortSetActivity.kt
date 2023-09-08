package com.mike.cn.controltab.ui.activity

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig.IP_ADDS
import com.mike.cn.controltab.app.ConnectConfig.PORT_NUM
import com.mike.cn.controltab.tools.UDPClient
import com.mike.cn.controltab.ui.base.BaseActivity
import com.tencent.mmkv.MMKV
import java.util.concurrent.Executors

/**
 * 连接端口设置页面
 */
class PortSetActivity : BaseActivity(), View.OnClickListener {


    var ipAdds: EditText? = null
    var portNum: EditText? = null
    var butSave: Button? = null
    val port = MMKV.defaultMMKV()
    var client: UDPClient? = null
    var ivBack: View? = null

    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_port_set)
    }

    override fun initView() {
        ipAdds = findViewById(R.id.ip_Adds)
        portNum = findViewById(R.id.port_Num)
        butSave = findViewById(R.id.but_Save)
        ivBack = findViewById(R.id.iv_back)
        butSave?.setOnClickListener(this)
        ivBack?.setOnClickListener(this)
    }

    override fun obtainData() {
        portNum?.setText(port.getInt(PORT_NUM, 9999).toString())
        ipAdds?.setText(port.getString(IP_ADDS, ""))

    }

    override fun initEvent() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.but_Save -> {
                if (ipAdds?.text.toString().trim().isEmpty()) {
                    Toast.makeText(context, "请输入 Ip 地址", Toast.LENGTH_SHORT).show()
                    return
                }
                if (portNum?.text.toString().trim().isEmpty()) {
                    Toast.makeText(context, "请输入 Ip 地址", Toast.LENGTH_SHORT).show()
                    return
                }
                savePort()
            }
            R.id.iv_back -> {
                onBackPressed()
            }

        }
    }

    private fun savePort() {
        port.putString(IP_ADDS, ipAdds?.text.toString())
        port.putInt(PORT_NUM, portNum?.text.toString().toInt())
        Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show()
        if (client == null || !client!!.isUdpLife) {
            //建立线程池
            val exec = Executors.newCachedThreadPool()
            client = UDPClient()
            client?.connect()
            exec.execute(client)
        }
        onBackPressed()
    }
}