package com.mike.cn.controltab.ui.activity

import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.mike.cn.controltab.R
import com.mike.cn.controltab.tools.UdpUtil
import com.mike.cn.controltab.ui.base.BaseActivity
import java.lang.String

class ScheduleControlActivity : BaseActivity(), View.OnClickListener {

    var seekBar: SeekBar? = null
    var percent: TextView? = null
    var ivBack: View? = null
    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_schedule_control)
    }

    override fun initView() {
        seekBar = findViewById(R.id.seekBar)
        percent = findViewById(R.id.percent)
        ivBack = findViewById(R.id.iv_back)
        ivBack?.setOnClickListener(this)
    }

    override fun obtainData() {
    }

    override fun initEvent() {
        seekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // 进度变化时的操作
                percent?.text = progress.toString()
//                EE B1 11 00 06 00 09 13 00 00 00 00 FF FC FF FF--
//                EE B1 11 00 06 00 09 13 00 00 00 64 FF FC FF FF 第39通道0-100背光光值对应255
                val hexadecimalNumber = String.format("%02X", progress)
                val allCode = "EE B1 11 00 06 00 09 13 00 00 00 $hexadecimalNumber FF FC FF FF"
                Log.i("111111", allCode)
                UdpUtil.getInstance().sendUdpCommand(allCode)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // 开始拖动时的操作
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // 停止拖动时的操作
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }

        }
    }
}

