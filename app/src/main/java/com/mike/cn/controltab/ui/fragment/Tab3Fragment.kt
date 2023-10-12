package com.mike.cn.controltab.ui.fragment

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mike.cn.controltab.R
import com.mike.cn.controltab.tools.UdpUtil
import com.tencent.mmkv.MMKV

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 点唱机控制
 */
class Tab3Fragment : Fragment() {
    var mediaPlayer: MediaPlayer? = null
    private var param1: String? = null
    private var param2: String? = null

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
        val v = inflater.inflate(R.layout.fragment_tab3, container, false)
        initView(v)
        return v
    }


    fun initView(view: View) {
        for (i in viewArrayId) {
            val vv: View = view.findViewById(i)
            viewArray.add(vv)
            vv.setOnClickListener {
                if (vv.tag != null)
                    UdpUtil.getInstance().sendUdpCommand(vv.tag.toString())
                playRaw()
                // 缩放动画
                it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                }).start()
            }
        }

        mediaPlayer = MediaPlayer.create(context, R.raw.tt)

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