package com.mike.cn.controltab.ui.fragment

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mike.cn.controltab.R
import com.mike.cn.controltab.tools.UdpUtil

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 空调控制
 */
class Tab4Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    var mediaPlayer: MediaPlayer? = null
    private var param1: String? = null
    private var param2: String? = null
    private var tv_wd: TextView? = null
    private var viewArrayId = arrayOf(
        R.id.but_p,
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

    private var viewArray = arrayListOf<View>()

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
        for (i in viewArrayId) {
            val vv: View = view.findViewById(i)
            viewArray.add(vv)
            vv.setOnClickListener {
                if (vv.tag != null)
                    UdpUtil.getInstance().sendUdpCommand(vv.tag.toString())
                weShow(vv)
                playRaw()
                // 缩放动画
                it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                }).start()
            }
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.tt)

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
}