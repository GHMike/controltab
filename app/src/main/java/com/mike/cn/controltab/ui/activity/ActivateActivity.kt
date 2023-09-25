package com.mike.cn.controltab.ui.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig
import com.mike.cn.controltab.tools.ConfigHelper
import com.mike.cn.controltab.ui.base.BaseActivity
import com.tencent.mmkv.MMKV


/**
 *激活页面
 */
@SuppressLint("SetTextI18n")
class ActivateActivity : BaseActivity() {


    private var tv_code: TextView? = null
    private var et_password: EditText? = null
    private var but_connect: Button? = null
    private var mmkv: MMKV? = null

    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_activate)
    }

    override fun initView() {
        mmkv = MMKV.defaultMMKV()
        tv_code = findViewById(R.id.tv_code)
        et_password = findViewById(R.id.et_password)
        but_connect = findViewById(R.id.but_connect)
    }

    override fun obtainData() {
        ConfigHelper().getEnCode()?.let { tv_code?.text = "设备 ID：${it}" }
    }

    override fun initEvent() {
        but_connect?.setOnClickListener {
            if (ConfigHelper().verifyCode(et_password?.text.toString()) == true) {
                mmkv?.putBoolean(ConnectConfig.IS_ACTIVATE, true)
                Toast.makeText(context, "激活成功！", Toast.LENGTH_LONG).show()
                val intent = Intent(this, IndexActivity::class.java)
                // 设置标志以关闭所有 Activity 并将新 Activity 设为任务的根 Activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                Toast.makeText(context, "验证码错误！", Toast.LENGTH_LONG).show()
            }
        }

        tv_code?.setOnClickListener {
            // 获取系统剪贴板管理器
            val clipboardManager: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 要复制的文本
            val textToCopy = ConfigHelper().getEnCode()
            // 创建一个 ClipData 对象
            val clipData = ClipData.newPlainText("text label", textToCopy)
            // 将 ClipData 对象复制到剪贴板
            clipboardManager.setPrimaryClip(clipData)
            // 提示用户已复制文本到剪贴板
            // 这一步是可选的，可以根据你的应用需求来决定是否提示用户
            Toast.makeText(this, "文本已复制到剪贴板", Toast.LENGTH_SHORT).show();
        }
    }

}