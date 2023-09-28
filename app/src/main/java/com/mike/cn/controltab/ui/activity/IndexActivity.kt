package com.mike.cn.controltab.ui.activity

import android.content.Intent
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig.IS_ACTIVATE
import com.mike.cn.controltab.app.ConnectConfig.VIDEO_PATH
import com.mike.cn.controltab.ui.base.BaseActivity
import com.tencent.mmkv.MMKV
import xyz.doikki.videoplayer.player.BaseVideoView.SCREEN_SCALE_MATCH_PARENT
import xyz.doikki.videoplayer.player.VideoView
import java.io.File


class IndexActivity : BaseActivity() {

    private var videoView: VideoView? = null
    private var vReturn: ImageView? = null
    private var default_iv: ImageView? = null


    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_index)
    }

    override fun initView() {
        val mmkv = MMKV.defaultMMKV()
        if (!mmkv.getBoolean(IS_ACTIVATE, false)) {
            val intent = Intent(this, ActivateActivity::class.java)
            // 设置标志以关闭所有 Activity 并将新 Activity 设为任务的根 Activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        videoView = findViewById(R.id.player)
        vReturn = findViewById(R.id.v_return)
        default_iv = findViewById(R.id.default_iv)
    }

    override fun obtainData() {
        setVideoPlayer()
        videoView!!.start() //开始播放，不调用则不自动播放
        Log.e("播放状态", videoView?.currentPlayState.toString())
    }

    override fun initEvent() {

        videoView?.setOnClickListener() {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        default_iv?.setOnClickListener() {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        vReturn?.setOnClickListener() {
            finish()
        }
    }


    /**
     * 设置播放器
     */
    fun setVideoPlayer() {

        // 获取下载文件夹目录
        val downloadDirectory =
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        // 读取文件
        val filePath = "$downloadDirectory/test.mp4" // 替换为实际的文件名
        val path = MMKV.defaultMMKV().getString(VIDEO_PATH, filePath)
        if (!File(path!!).exists())
            default_iv?.visibility = View.VISIBLE
        else
            default_iv?.visibility = View.GONE
        //静音
        videoView?.isMute = true
        //循环播放
        videoView?.setLooping(true)
        //设置满屏
        videoView?.setScreenScaleType(SCREEN_SCALE_MATCH_PARENT)
        videoView!!.setUrl(path) //设置视频地址
        videoView?.release()
        videoView?.start()
    }

    override fun onBackPressed() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (videoView != null) {
            videoView?.release()
        }
    }

    override fun onPause() {
        super.onPause()
        if (videoView != null) {
            videoView?.pause()
        }
    }


    override fun onResume() {
        super.onResume()
        if (videoView != null) {
            setVideoPlayer()
//            videoView?.resume()
        }
    }
}