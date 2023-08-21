package com.mike.cn.controltab.ui.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mike.cn.controltab.R
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.ConfigHelper
import com.mike.cn.controltab.ui.adapters.MenuAdapter
import com.mike.cn.controltab.ui.dialog.CustomDialog

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Tab1Fragment : Fragment(), View.OnClickListener, CustomDialog.OnButtonClickListener {


    var mediaPlayer: MediaPlayer? = null
    private var param1: String? = null
    private var param2: String? = null

    var rvData: RecyclerView? = null
    var myAdapter: MenuAdapter? = null
    var but1: TextView? = null
    var but2: TextView? = null

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
        val v = inflater.inflate(R.layout.fragment_tab1, container, false)
        initW(v)
        return v
    }


    fun initW(v: View) {
        rvData = v.findViewById(R.id.rv_data)
        but1 = v.findViewById(R.id.but1)
        but2 = v.findViewById(R.id.but2)
        but1?.setOnClickListener(this)
        but2?.setOnClickListener(this)
        rvData?.layoutManager = GridLayoutManager(context, 3)
        myAdapter = MenuAdapter()
        rvData?.adapter = myAdapter
        myAdapter?.setOnItemLongClickListener { _, _, position ->
            showCustomDialog(myAdapter!!.getItem(position))
            true
        }
        myAdapter?.setOnItemClickListener() { _, view, position ->
            playRaw()
            // 缩放动画
            view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }).start()
        }
        initData()
        mediaPlayer = MediaPlayer.create(context, R.raw.tt)
    }

    fun initData() {
        myAdapter?.setList(ConfigHelper().getConfigMenuList("1"))
    }

    private fun showCustomDialog(data: MenuInfoModel) {
        val dialog = CustomDialog(activity, data, this)
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Tab1Fragment().apply {
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
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.but2 -> {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
        ConfigHelper().upDataConfigMenuList(infoModel)
        Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show()
        initData()
    }
}