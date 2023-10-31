package com.mike.cn.controltab.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.mike.cn.controltab.BuildConfig
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig
import com.mike.cn.controltab.tools.FileHelper
import com.mike.cn.controltab.ui.base.BaseActivity
import com.mike.cn.controltab.ui.dialog.PasswordInputDialog
import com.tencent.mmkv.MMKV

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
    var but_sys: View? = null

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
        but_sys = findViewById(R.id.but_sys)
        ivBack?.setOnClickListener(this)
        but3?.setOnClickListener(this)
        but3_1?.setOnClickListener(this)
        but4?.setOnClickListener(this)
        but5?.setOnClickListener(this)
        but_sys?.setOnClickListener(this)
//        but_sys?.visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.GONE

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
            //重置配置文件
            R.id.but_sys -> {
                showConfirmationDialog()
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

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("确认操作")
        builder.setMessage("您确定要执行此操作吗？")

        // 确认按钮
        builder.setPositiveButton("确定") { dialog, _ ->
            val defaultInfo = MMKV.defaultMMKV()
            val config: String = FileHelper().getTxtContent(context, "config.txt")
            val config2: String = FileHelper().getTxtContent(context, "config2.txt")
            defaultInfo.encode("config", config)
            defaultInfo.encode("config2", config2)
            Toast.makeText(this, "重置成功，重新回到视频播放页！", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        // 取消按钮
        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }
        // 创建并显示弹窗
        val dialog = builder.create()
        dialog.show()
    }
}