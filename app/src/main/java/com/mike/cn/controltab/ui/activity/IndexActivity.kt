package com.mike.cn.controltab.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig.IS_ACTIVATE
import com.mike.cn.controltab.app.ConnectConfig.VIDEO_PATH
import com.mike.cn.controltab.tools.FileHelper
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
//        val intent = Intent(Intent.ACTION_MAIN)
//        intent.addCategory(Intent.CATEGORY_HOME)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
    }

    override fun initView() {
        val mmkv = MMKV.defaultMMKV()
        if (!mmkv.getBoolean(IS_ACTIVATE, false)) {
            val intent = Intent(this, ActivateActivity::class.java)
            // 设置标志以关闭所有 Activity 并将新 Activity 设为任务的根 Activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        requestBootPermission()

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

        // 读取文件
        FileHelper().copyAssetToInternalStorage(this, "mainVideo.mp4")
        val filePath: String = FileHelper().getInternalStoragePath(this, "mainVideo.mp4")
        Log.e("默认路径", filePath)
        val path = MMKV.defaultMMKV().getString(VIDEO_PATH, filePath)
        if (!File(path!!).exists() && !File(filePath).exists())
            default_iv?.visibility = View.VISIBLE
        else
            default_iv?.visibility = View.GONE
        //静音
        videoView?.isMute = true
        //循环播放
        videoView?.setLooping(true)
        //设置满屏
        videoView?.setScreenScaleType(SCREEN_SCALE_MATCH_PARENT)
        if (File(path).exists())
            videoView!!.setUrl(path) //设置视频地址
        else
            videoView!!.setUrl(filePath) //设置初始视频地址
        videoView?.release()
        videoView?.start()
    }

    override fun onBackPressed() {

    }

    // 请求 RECEIVE_BOOT_COMPLETED 权限
    private fun requestBootPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val permission = checkSelfPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 如果权限没有授予，则请求权限
                requestPermissions(arrayOf(Manifest.permission.RECEIVE_BOOT_COMPLETED), 1)
            }
        }
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