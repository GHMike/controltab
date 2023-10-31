package com.mike.cn.controltab.ui.fragment

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.ConfigHelper
import com.mike.cn.controltab.tools.FileHelper
import com.mike.cn.controltab.tools.UdpUtil
import com.mike.cn.controltab.ui.adapters.MenuAdapter
import com.mike.cn.controltab.ui.dialog.CustomDialog
import com.mike.cn.controltab.ui.dialog.MyPopupWindow
import com.tencent.mmkv.MMKV
import razerdp.basepopup.BasePopupWindow


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("SetTextI18n")
class MainFragment : Fragment(), View.OnClickListener, CustomDialog.OnButtonClickListener {


    private var mediaPlayer: MediaPlayer? = null
    private var param1: String? = null
    private var param2: String? = null

    private var but1: TextView? = null
    private var but2: TextView? = null
    private var tv_code1: TextView? = null
    private var tv_code2: TextView? = null
    private var but3: View? = null
    private var but4: View? = null
    private var dialog: CustomDialog? = null
    private var mCustomPopWindow: BasePopupWindow? = null
    private var isEdit = MMKV.defaultMMKV().getBoolean(ConnectConfig.IS_EDIT, false)

    //临时存储弹窗数据类型
    private var popupWindowDataType = ""
    private var myPopAdapter = MenuAdapter()


    private var viewArrayId = arrayOf(
        R.id.but_1,
        R.id.but_2,
        R.id.but_3,
        R.id.but_4,
        R.id.but_5,
        R.id.but_6,
    )
    private var nameVArrayId = arrayOf(
        R.id.tv_name1,
        R.id.tv_name2,
        R.id.tv_name3,
        R.id.tv_name4,
        R.id.tv_name5,
        R.id.tv_name6,
    )
    private var imaVArrayId = arrayOf(
        R.id.iv_img1,
        R.id.iv_img2,
        R.id.iv_img3,
        R.id.iv_img4,
        R.id.iv_img5,
        R.id.iv_img6,
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
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        initW(v)
        return v
    }


    private fun initW(v: View) {
        but1 = v.findViewById(R.id.but1)
        but2 = v.findViewById(R.id.but2)
        tv_code1 = v.findViewById(R.id.tv_code1)
        tv_code2 = v.findViewById(R.id.tv_code2)
        but3 = v.findViewById(R.id.but3)
        but4 = v.findViewById(R.id.but4)
        but1?.setOnClickListener(this)
        but2?.setOnClickListener(this)
        but3?.setOnClickListener(this)
        but4?.setOnClickListener(this)

        //循环找控件
        viewArrayId.forEachIndexed { index, i ->
            val vv: View = v.findViewById(viewArrayId[index])
            viewArray.add(vv)
            val namev: TextView = v.findViewById(nameVArrayId[index])
            nameVArray.add(namev)
            val imgv: ImageView = v.findViewById(imaVArrayId[index])
            imgVArray.add(imgv)

            vv.setOnClickListener {
                val nameV = nameVArray[viewArray.indexOf(it)]
                val vv = viewArray[viewArray.indexOf(it)]

                when (vv.tag) {
                    "1" -> {
                        showPop(vv, "kg")
                    }
                    "5" -> {
                        showPop(vv, "DJ")
                    }
                    "6" -> {
                        showPop(vv, "cjx")
                    }
                    "7" -> {
                        showPop(vv, "temp1")
                    }
                    "8" -> {
                        showPop(vv, "temp2")
                    }
                    "16" -> {
                        showPop(vv, "temp3")
                    }
                    else -> {

                    }
                }
                if (nameV.tag != null) {
                    UdpUtil.getInstance().sendUdpCommand(nameV.tag.toString())
                }

                playRaw()
                playAn(it)
            }

            vv.setOnLongClickListener {
                if (isEdit) {
                    showCustomDialog(viewArray.indexOf(it))
                }
                true
            }
        }
//        myAdapter?.setOnItemLongClickListener { _, _, position ->
//            val item = myAdapter?.getItem(position)
//            if (isEdit) {
////                if (item?.id != "1" && item?.id != "5" && item?.id != "6") {
//                showCustomDialog(myAdapter!!.getItem(position))
////                }
//            }
//            true
//        }
//        myAdapter?.setOnItemClickListener() { adapter, view, position ->
//            val item = myAdapter?.getItem(position)
//            when (item?.id) {
//                "1" -> {
//                    showPop(view, "kg")
//                }
//                "5" -> {
//                    showPop(view, "DJ")
//                }
//                "6" -> {
//                    showPop(view, "cjx")
//                }
//                else -> {
//                    UdpUtil.getInstance().sendUdpCommand(myAdapter?.getItem(position)?.code)
//                }
//            }
//            playRaw()
//            playAn(view)
//
//        }
        initData()
        mediaPlayer = MediaPlayer.create(context, R.raw.tt)
        but1?.setOnLongClickListener {
            if (isEdit) {
                showCustomDialog(but1!!)
            }
            true
        }
        but2?.setOnLongClickListener {
            if (isEdit) {
                showCustomDialog(but2!!)
            }
            true
        }


    }


    private fun showPop(v: View, type: String) {
        //创建并显示popWindow
        mCustomPopWindow = MyPopupWindow(this)
        mCustomPopWindow?.setContentView(R.layout.popup_layout)
        setPopupViewAndData(mCustomPopWindow?.contentView!!, type)
        mCustomPopWindow?.setBlurBackgroundEnable(true)
        mCustomPopWindow?.showPopupWindow()
    }

    private fun initData() {
        //获取固定配置
        val infoList = ConfigHelper().getConfigFixedMenuList("3")
        for (j in infoList) {
            if (j.id == "26") {
                but1?.text = j.name
                tv_code1?.tag = j.code
                tv_code1?.text = j.image
            }
            if (j.id == "27") {
                but2?.text = j.name
                tv_code2?.tag = j.code
                tv_code2?.text = j.image
            }
        }

        val mainList = ConfigHelper().getConfigMenuList("1")
        viewArray.forEachIndexed { index, view ->
            val vv = viewArray[index]
            val nv = nameVArray[index]
            val iv = imgVArray[index]

            for (j in mainList) {
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

    //列表修改弹窗
    private fun showCustomDialog(data: MenuInfoModel) {
        if (dialog == null) {
            dialog = CustomDialog(activity, data, this)
        } else {
            dialog?.setData(data)
        }
        dialog?.show()
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

    //显示固定菜单修改窗
    private fun showCustomDialog(view: TextView) {
        val data = MenuInfoModel(0)
        data.id = view.tag.toString()
        data.type = "3"
        data.name = view.text.toString()
        if (view.tag.toString() == "26") {
            data.code = tv_code1?.tag.toString()
            data.image = tv_code1?.text.toString()
        } else {
            data.code = tv_code2?.tag.toString()
            data.image = tv_code2?.text.toString()
        }
        if (dialog == null) {
            dialog = CustomDialog(activity, data, this)
        } else {
            dialog?.setData(data)
        }
        dialog?.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        playRaw()
        when (v?.id) {
            R.id.but1 -> {
                try {
                    if (tv_code1?.tag != null) {
                        UdpUtil.getInstance().sendUdpCommand(tv_code1?.tag.toString())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.but2 -> {
                try {
                    if (tv_code2?.tag != null) {
                        UdpUtil.getInstance().sendUdpCommand(tv_code2?.tag.toString())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.but3 -> {
                playRaw()
                playAn(v)
                showPop(v, "hzh")
            }
            R.id.but4 -> {
                playRaw()
                playAn(v)
                showPop(v, "yykz")
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
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    /**
     * 播放音效
     */
    private fun playRaw() {
        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(0)
            mediaPlayer!!.start()
        }
    }

    /**
     * 播放动画
     */
    private fun playAn(view: View) {
        // 缩放动画
        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
        }).start()
    }


    //设置popup 的控件和数据
    private fun setPopupViewAndData(v: View, type: String) {
        popupWindowDataType = type
        val rvPopData: RecyclerView = v.findViewById(R.id.rv_data)
        val vVolume: View = v.findViewById(R.id.v_volume)
        val sbVolume: SeekBar = v.findViewById(R.id.sb_volume)
        val tvVolume: TextView = v.findViewById(R.id.tv_volume)

        val VOLUME = MMKV.defaultMMKV().getInt(ConnectConfig.VOLUME, 0)
        sbVolume.progress = VOLUME
        tvVolume.text = "${VOLUME}%"
        if (type == "yykz") {
            vVolume.visibility = View.VISIBLE
        } else {
            vVolume.visibility = View.GONE
        }

        var countNum = 3
        when (type) {
            "cjx", "kg", "yykz" ->
                countNum = 5
            "temp1", "temp2", "temp3" ->
                countNum = 2
        }

        val layoutManager = GridLayoutManager(context, countNum)
        // 设置居中对齐
        rvPopData.layoutManager = layoutManager

        myPopAdapter = MenuAdapter()
        rvPopData.adapter = myPopAdapter
        myPopAdapter.setOnItemLongClickListener { _, _, position ->
            if (isEdit)
                showCustomDialog(myPopAdapter.getItem(position))
            true
        }
        myPopAdapter.setOnItemClickListener() { adapter, view, position ->
            val item = myPopAdapter.getItem(position)
            UdpUtil.getInstance().sendUdpCommand(item.code)
            playRaw()
            playAn(view)
        }
        //音量
        sbVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tvVolume.text = "${progress}%"
                /* 进度变化时的操作 */
                val hexadecimalNumber = java.lang.String.format("%02X", progress)
                val allCode = "EE B1 11 00 06 00 0A 13 00 00 00 $hexadecimalNumber FF FC FF FF"
                Log.i("111111", allCode + progress.toString())
                UdpUtil.getInstance().sendUdpCommand(allCode)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // 开始拖动时的操作
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // 停止拖动时的操作
                MMKV.defaultMMKV().putInt(ConnectConfig.VOLUME, seekBar.progress)
            }
        })

        myPopAdapter.setList(ConfigHelper().getConfigMenuList(type))
    }

    /**
     * 刷新弹窗数据
     */
    private fun resPopupData() {
        myPopAdapter.setList(ConfigHelper().getConfigMenuList(popupWindowDataType))
    }

    override fun onPositiveButtonClick() {
    }

    override fun onNegativeButtonClick(infoModel: MenuInfoModel) {
        if (infoModel.type == "3") {
            ConfigHelper().upDataConfigFixedMenuList(infoModel)
        } else {
            ConfigHelper().upDataConfigMenuList(infoModel)
        }
        Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show()
        resPopupData()
        initData()
    }

    override fun onResume() {
        super.onResume()
        isEdit = MMKV.defaultMMKV().getBoolean(ConnectConfig.IS_EDIT, false)
    }
}