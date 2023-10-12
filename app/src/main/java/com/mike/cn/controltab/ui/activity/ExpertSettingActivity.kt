package com.mike.cn.controltab.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig
import com.mike.cn.controltab.app.ConnectConfig.IP_ADDS
import com.mike.cn.controltab.app.ConnectConfig.PORT_NUM
import com.mike.cn.controltab.tools.UDPClient
import com.mike.cn.controltab.ui.base.BaseActivity
import com.mike.cn.controltab.ui.dialog.PasswordInputDialog
import com.tencent.mmkv.MMKV
import java.util.concurrent.Executors

/**
 * 连接端口设置页面
 */
@SuppressLint("UseSwitchCompatOrMaterialCode")
class ExpertSettingActivity : BaseActivity(), View.OnClickListener {


    var but3: View? = null
    var but3_1: View? = null
    var but4: View? = null
    var but5: View? = null
    var sEdit: Switch? = null
    var ivBack: View? = null

    val pathData = MMKV.defaultMMKV()

    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_exper_set)
    }

    override fun initView() {
        ivBack = findViewById(R.id.iv_back)
        but3 = findViewById(R.id.but3)
        but3_1 = findViewById(R.id.but3_1)
        but4 = findViewById(R.id.but4)
        but5 = findViewById(R.id.but5)
        sEdit = findViewById(R.id.s_edit)
        ivBack?.setOnClickListener(this)
        but3?.setOnClickListener(this)
        but3_1?.setOnClickListener(this)
        but4?.setOnClickListener(this)
        but5?.setOnClickListener(this)

        val isEdit = MMKV.defaultMMKV().getBoolean(ConnectConfig.IS_EDIT, false)
        sEdit?.isChecked = isEdit
    }

    override fun obtainData() {
    }

    override fun initEvent() {
        sEdit?.setOnCheckedChangeListener { v, check ->
            MMKV.defaultMMKV().putBoolean(ConnectConfig.IS_EDIT, check)
        }
    }

    override fun onClick(v: View?) {
        playAnimate(v)
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
            R.id.but5 -> {
                //退出 app
                finishAffinity()
            }
            R.id.but3 -> {
                val intent = Intent(context, PortSetActivity::class.java)
                context?.startActivity(intent)
            }
            R.id.but4 -> {
                PasswordInputDialog(context, 1)
            }
            R.id.but3_1 -> {
                PictureSelector.create(this)
                    .openSystemGallery(SelectMimeType.ofVideo())
                    .setSelectionMode(SelectModeConfig.SINGLE)
                    .forSystemResult(object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>) {
                            try {
                                val paths = result[0]
                                val realPath = paths.realPath
                                // 获取文件名中最后一个点（.）的位置
                                val lastIndex: Int = realPath.lastIndexOf(".")
                                var fileExtension = ""
                                if (lastIndex != -1) {
                                    // 从最后一个点的位置开始截取字符串，得到后缀名
                                    fileExtension = realPath.substring(lastIndex + 1)
                                }

                                if (fileExtension.contains("mp4") || fileExtension.contains("MP4") || fileExtension.contains(
                                        "avi"
                                    ) || fileExtension.contains("AVI") ||
                                    fileExtension.contains("mkv") || fileExtension.contains("MKV") || fileExtension.contains(
                                        "wmv"
                                    ) || fileExtension.contains("WMV")
                                ) {
                                    pathData.putString(ConnectConfig.VIDEO_PATH, realPath)
                                    Toast.makeText(context, "设置完成", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "请选择正确的视频格式", Toast.LENGTH_LONG).show()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onCancel() {}
                    })
            }
        }
    }

}