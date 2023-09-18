package com.mike.cn.controltab.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.ToastUtils
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig
import com.mike.cn.controltab.app.ConnectConfig.IS_EDIT
import com.mike.cn.controltab.ui.activity.PortSetActivity
import com.mike.cn.controltab.ui.activity.ScheduleControlActivity
import com.mike.cn.controltab.ui.activity.WifiActivity
import com.tencent.mmkv.MMKV


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("UseSwitchCompatOrMaterialCode")
class SettingFragment : Fragment(), View.OnClickListener {
    private var param1: String? = null
    private var param2: String? = null
    val pathData = MMKV.defaultMMKV()

    var but1: View? = null
    var but2: View? = null
    var but3: View? = null
    var but3_1: View? = null
    var but4: View? = null

    var vPass: View? = null
    var pass: EditText? = null
    var butCom: Button? = null
    var sEdit: Switch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        initData()
    }

    fun initW(con: View) {
        but1 = con.findViewById(R.id.but1)
        but2 = con.findViewById(R.id.but2)
        but3 = con.findViewById(R.id.but3)
        but3_1 = con.findViewById(R.id.but3_1)
        but4 = con.findViewById(R.id.but4)

        vPass = con.findViewById(R.id.v_pass)
        pass = con.findViewById(R.id.edit_pass)
        butCom = con.findViewById(R.id.but_com)
        sEdit = con.findViewById(R.id.s_edit)

        but1?.setOnClickListener(this)
        but2?.setOnClickListener(this)
        but3?.setOnClickListener(this)
        but3_1?.setOnClickListener(this)
        but4?.setOnClickListener(this)
        butCom?.setOnClickListener(this)

        val isEdit = MMKV.defaultMMKV().getBoolean(IS_EDIT, false)
        sEdit?.isChecked = isEdit

        sEdit?.setOnCheckedChangeListener { v, check ->
            MMKV.defaultMMKV().putBoolean(IS_EDIT, check)
        }
    }


    fun initData() {

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.fragment_setting, container, false)
        initW(v)
        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        if (vPass != null) {
            vPass?.visibility = View.VISIBLE
            pass?.setText("")
        }
    }


    override fun onClick(v: View?) {
        animate(v!!)
        Thread.sleep(500)
        if (vPass?.visibility == View.VISIBLE && v.id != R.id.but_com)
            return
        when (v.id) {
            R.id.but1 -> {
                val intent = Intent(context, ScheduleControlActivity::class.java)
                context?.startActivity(intent)
            }
            R.id.but2 -> {
                val intent = Intent(context, WifiActivity::class.java)
                context?.startActivity(intent)

            }
            R.id.but3 -> {
                val intent = Intent(context, PortSetActivity::class.java)
                context?.startActivity(intent)
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
            R.id.but4 -> {
                ToastUtils.showToast(context, "已经是最新版本了！")
                //UpdateInfo信息可以通过版本更新接口获取
//                val downloadUrl = "https://dx16.198449.com/com.xiaoji.tvbox.apk"
//                val manager = activity?.let {
//                    DownloadManager.Builder(it).run {
//                        apkUrl(downloadUrl)
//                        apkName("appupdate.apk")
//                        smallIcon(R.mipmap.ic_launcher)
//                        //设置了此参数，那么内部会自动判断是否需要显示更新对话框，否则需要自己判断是否需要更新
//                        apkVersionCode(attr.versionCode)
//                        //同时下面三个参数也必须要设置
//                        apkVersionName(attr.versionName.toString())
////                        apkSize("7.7MB")
//                        apkDescription("系统更新")
//
//                        //省略一些非必须参数...
//                        build()
//                    }
//                }
//                manager?.download()
            }
            R.id.but_com -> {
                if (pass?.text.toString() == ("123456")) {
                    vPass?.visibility = View.GONE
                } else {
                    Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    //播放动画
    private fun animate(view: View) {
        // 缩放动画
        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
        }).start()
    }
}