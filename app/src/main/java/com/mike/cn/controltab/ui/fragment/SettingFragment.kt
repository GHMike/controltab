package com.mike.cn.controltab.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.ToastUtils
import com.mike.cn.controltab.BuildConfig
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig
import com.mike.cn.controltab.app.ConnectConfig.IS_EDIT
import com.mike.cn.controltab.app.MyApp
import com.mike.cn.controltab.tools.FileHelper
import com.mike.cn.controltab.ui.activity.ExpertSettingActivity
import com.mike.cn.controltab.ui.activity.PortSetActivity
import com.mike.cn.controltab.ui.activity.ScheduleControlActivity
import com.mike.cn.controltab.ui.activity.WifiActivity
import com.mike.cn.controltab.ui.dialog.PasswordInputDialog
import com.tencent.mmkv.MMKV


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("UseSwitchCompatOrMaterialCode,SetTextI18n")
class SettingFragment : Fragment(), View.OnClickListener {
    private var param1: String? = null
    private var param2: String? = null
    val pathData = MMKV.defaultMMKV()

    var but1: View? = null
    var but2: View? = null
    var but3: View? = null
    var but4: View? = null
    var but5: View? = null

    var vPass: View? = null
    var pass: EditText? = null
    var butCom: Button? = null
    var ver: TextView? = null


    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    fun initW(con: View) {
        but1 = con.findViewById(R.id.but1)
        but2 = con.findViewById(R.id.but2)
        but3 = con.findViewById(R.id.but3)
        but4 = con.findViewById(R.id.but4)
        but5 = con.findViewById(R.id.but5)

        vPass = con.findViewById(R.id.v_pass)
        pass = con.findViewById(R.id.edit_pass)
        butCom = con.findViewById(R.id.but_com)
        ver = con.findViewById(R.id.ver)

        but1?.setOnClickListener(this)
        but2?.setOnClickListener(this)
        but3?.setOnClickListener(this)
        but4?.setOnClickListener(this)
        but5?.setOnClickListener(this)
        butCom?.setOnClickListener(this)
        mediaPlayer = MediaPlayer.create(context, R.raw.tt)
        ver?.text = "V${BuildConfig.VERSION_NAME}"
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


    override fun onClick(v: View?) {
        animate(v!!)
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
                PasswordInputDialog(requireContext(), 0)
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
            R.id.but5 -> {
                val defaultInfo = MMKV.defaultMMKV()
                val config: String = FileHelper().getTxtContent(requireActivity(), "config.txt")
                defaultInfo.encode("config", config)
            }

        }
    }

    //播放动画
    private fun animate(view: View) {
        // 缩放动画
        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
        }).start()

        playRaw()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放MediaPlayer资源
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    /**
     * 播放音效
     */
    fun playRaw() {
        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(0)
            mediaPlayer!!.start()
        }
    }
}