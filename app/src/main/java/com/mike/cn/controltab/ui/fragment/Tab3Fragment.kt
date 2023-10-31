package com.mike.cn.controltab.ui.fragment

import android.media.Image
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
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.ConfigHelper
import com.mike.cn.controltab.tools.FileHelper
import com.mike.cn.controltab.tools.UdpUtil
import com.mike.cn.controltab.ui.dialog.CustomDialog
import com.tencent.mmkv.MMKV

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 点唱机控制
 */
class Tab3Fragment : Fragment(), CustomDialog.OnButtonClickListener {
    var mediaPlayer: MediaPlayer? = null
    private var param1: String? = null
    private var param2: String? = null
    private var isEdit = MMKV.defaultMMKV().getBoolean(ConnectConfig.IS_EDIT, false)
    private var dialog: CustomDialog? = null

    private var viewArrayId = arrayOf(
        R.id.but1,
        R.id.but2,
        R.id.but3,
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
        val v = inflater.inflate(R.layout.fragment_tab3, container, false)
        initView(v)
        return v
    }


    fun initView(view: View) {
//        for (i in viewArrayId) {
//            val vv: View = view.findViewById(i)
//            viewArray.add(vv)
//            vv.setOnClickListener {
//                if (vv.tag != null)
//                    UdpUtil.getInstance().sendUdpCommand(vv.tag.toString())
//                playRaw()
//                // 缩放动画
//                it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
//                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
//                }).start()
//            }
//        }
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



        mediaPlayer = MediaPlayer.create(context, R.raw.tt)
        setData()
    }


    //设置显示数据
    fun setData() {
        val infoList = ConfigHelper().getConfigFixedMenuList("1")

        viewArray.forEachIndexed { index, view ->
            val vv = viewArray[index]
            val nv = nameVArray[index]
            val iv = imgVArray[index]

            for (j in infoList) {
                if (j.id == vv.tag) {
                    nv.text = j.name
                    nv.tag = j.code
                    iv.tag = j.image
                    activity?.let {
                        val resId = FileHelper().getMipmapResId(it, j.image!!)
                        if (resId != R.mipmap.ic_launcher_round) {
                            Glide.with(it).load(resId).error(R.mipmap.ic_launcher_round)
                                .into(iv)
                        } else {
                            Glide.with(it).load(j.image).error(R.mipmap.ic_launcher_round)
                                .into(iv)
                        }

                    }
//                    break
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Tab3Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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
        data.type = "1"
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