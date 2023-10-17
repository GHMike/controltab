package com.mike.cn.controltab.ui.fragment

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig
import com.mike.cn.controltab.app.ConnectConfig.TEMPERATURE_TAG
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.ConfigHelper
import com.mike.cn.controltab.tools.UdpUtil
import com.mike.cn.controltab.ui.dialog.CustomDialog
import com.tencent.mmkv.MMKV

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 空调控制
 */
class Tab4Fragment : Fragment(), CustomDialog.OnButtonClickListener {
    // TODO: Rename and change types of parameters
    var mediaPlayer: MediaPlayer? = null
    private var param1: String? = null
    private var param2: String? = null
    private var tv_wd: TextView? = null
    private var but_p: TextView? = null
    private var but_auto: TextView? = null
    private var v_p: TextView? = null
    private var v_auto: TextView? = null

    private var isEdit = MMKV.defaultMMKV().getBoolean(ConnectConfig.IS_EDIT, false)
    private var dialog: CustomDialog? = null

    private var mmvk: MMKV? = MMKV.defaultMMKV()

    private var viewArrayId = arrayOf(
        R.id.but_on,
        R.id.but_off,
        R.id.but_jia,
        R.id.but_jian,
        R.id.but4,
        R.id.but5,
        R.id.but6,
        R.id.but7,
        R.id.but8,
        R.id.but9,
        R.id.but10,
        R.id.but11
    )

    private var nameVArrayId = arrayOf(
        R.id.tv_name,
        R.id.tv_name1,
        R.id.tv_name2,
        R.id.tv_name3,
        R.id.tv_name4,
        R.id.tv_name5,
        R.id.tv_name6,
        R.id.tv_name7,
        R.id.tv_name8,
        R.id.tv_name9,
        R.id.tv_name10,
        R.id.tv_name11
    )
    private var imaVArrayId = arrayOf(
        R.id.iv_img,
        R.id.iv_img1,
        R.id.iv_img2,
        R.id.iv_img3,
        R.id.iv_img4,
        R.id.iv_img5,
        R.id.iv_img6,
        R.id.iv_img7,
        R.id.iv_img8,
        R.id.iv_img9,
        R.id.iv_img10,
        R.id.iv_img11
    )

    private var viewArray = arrayListOf<View>()
    private var nameVArray = arrayListOf<TextView>()
    private var imgVArray = arrayListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_tab4, container, false)
        initView(v)
        return v
    }

    fun initView(view: View) {
        tv_wd = view.findViewById(R.id.tv_wd)
        but_p = view.findViewById(R.id.but_p)
        but_auto = view.findViewById(R.id.but_auto)
        v_p = view.findViewById(R.id.v_p)
        v_auto = view.findViewById(R.id.v_auto)
        but_p?.setOnClickListener {
            try {
                if (v_p?.tag != null) {
                    UdpUtil.getInstance().sendUdpCommand(v_p?.tag.toString())
                }
                playRaw()
                // 缩放动画
                it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                }).start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        but_auto?.setOnClickListener {
            try {
                if (v_auto?.tag != null) {
                    UdpUtil.getInstance().sendUdpCommand(v_auto?.tag.toString())
                }
                playRaw()
                // 缩放动画
                it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                }).start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        but_p?.setOnLongClickListener {
            if (isEdit) {
                showCustomDialog(but_p!!)
            }
            true
        }
        but_auto?.setOnLongClickListener {
            if (isEdit) {
                showCustomDialog(but_auto!!)
            }
            true
        }
        //循环找控件
        viewArrayId.forEachIndexed { index, i ->
            val vv: View = view.findViewById(viewArrayId[index])
            viewArray.add(vv)
            val namev: TextView = view.findViewById(nameVArrayId[index])
            nameVArray.add(namev)
            val imgv: ImageView = view.findViewById(imaVArrayId[index])
            imgVArray.add(imgv)

            vv.setOnClickListener {
                val nameV = nameVArray[viewArray.indexOf(it)]
                if (nameV.tag != null) {
                    UdpUtil.getInstance().sendUdpCommand(nameV.tag.toString())
                }
                playRaw()
                weShow(vv)
                // 缩放动画
                it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                }).start()
            }

            vv.setOnLongClickListener {
                if (isEdit) {
                    showCustomDialog(viewArray.indexOf(it))
                }
                true
            }
        }

        tv_wd?.text = mmvk?.getString(TEMPERATURE_TAG, "22")
        mediaPlayer = MediaPlayer.create(context, R.raw.tt)
        setData()
    }

    //设置显示数据
    fun setData() {
        val infoList = ConfigHelper().getConfigFixedMenuList("2")
        viewArray.forEachIndexed { index, view ->
            val vv = viewArray[index]
            val nv = nameVArray[index]
            val iv = imgVArray[index]

            for (j in infoList) {
                when (j.id) {
                    vv.tag -> {
                        nv.text = j.name
                        nv.tag = j.code
                        iv.tag = j.image
                        activity?.let {
                            Glide.with(it).load(j.image).error(R.mipmap.ic_launcher_round)
                                .into(iv)
                        }
                    }
                    "12" -> {
                        but_p?.text = j.name
                        v_p?.text = j.image
                        v_p?.tag = j.code
                    }
                    "25" -> {
                        but_auto?.text = j.name
                        v_auto?.text = j.image
                        v_auto?.tag = j.code
                    }
                }

            }
        }
    }

    //显示修改窗
    private fun showCustomDialog(index: Int) {
        val data = MenuInfoModel(0)
        val vv = viewArray[index]
        val name = nameVArray[index]
        val imag = imgVArray[index]
        data.id = vv.tag.toString()
        data.type = "2"
        data.code = name.tag.toString()
        data.name = name.text.toString()
        data.image = imag.tag.toString()
        if (dialog == null) {
            dialog = CustomDialog(activity, data, this)
        } else {
            dialog?.setData(data)
        }
        dialog?.show()
    }

    //单个固定菜单修改窗
    private fun showCustomDialog(view: TextView) {
        val data = MenuInfoModel(0)
        data.id = view.tag.toString()
        data.type = "3"
        data.name = view.text.toString()
        if (view.tag.toString() == "12") {
            data.code = v_p?.tag.toString()
            data.image = v_p?.text.toString()
        } else if (view.tag.toString() == "25") {
            data.code = v_auto?.tag.toString()
            data.image = v_auto?.text.toString()
        }
        if (dialog == null) {
            dialog = CustomDialog(activity, data, this)
        } else {
            dialog?.setData(data)
        }
        dialog?.show()
    }

    /**
     * 如果是温度加减就操作显示
     */
    private fun weShow(v: View) {
        var wd = tv_wd?.text.toString().toInt()
        if (v.id == R.id.but_jia) {
            if (wd < 32)
                wd++
        } else if (v.id == R.id.but_jian) {
            if (wd > 16)
                wd--
        }
        mmvk?.putString(TEMPERATURE_TAG, wd.toString())
        tv_wd?.text = wd.toString()

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Tab4Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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

    override fun onPositiveButtonClick() {
    }

    override fun onNegativeButtonClick(infoModel: MenuInfoModel) {
        ConfigHelper().upDataConfigFixedMenuList(infoModel)
        Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show()
        setData()
    }

    override fun onResume() {
        isEdit = MMKV.defaultMMKV().getBoolean(ConnectConfig.IS_EDIT, false)
        super.onResume()

    }
}