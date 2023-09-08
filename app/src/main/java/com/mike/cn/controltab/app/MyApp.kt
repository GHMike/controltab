package com.mike.cn.controltab.app

import android.app.Application
import android.util.Log
import com.jeremyliao.liveeventbus.LiveEventBus
import com.mike.cn.controltab.BuildConfig
import com.mike.cn.controltab.tools.FileHelper
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import xyz.doikki.videoplayer.player.AndroidMediaPlayerFactory
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager


class MyApp : Application() {

    private var instance: MyApp? = null

    /**
     * 单例模式中获取唯一的MyApplication实例
     *
     * @return
     */
    fun getInstance(): MyApp? {
        if (null == instance) {
            instance = MyApp()
        }
        return instance
    }


    override fun onCreate() {
        super.onCreate()
        if (null == instance) {
            instance = this
        }

        MMKV.initialize(instance)


        LiveEventBus.config() //在没有Observer关联的时候是否自动清除LiveEvent以释放内存（默认值false）
            .autoClear(true) //配置支持跨进程、跨APP通信
            .enableLogger(BuildConfig.DEBUG) //配置LifecycleObserver（如Activity）接收消息的模式： true：整个生命周期（从onCreate到onDestroy）都可以实时收到消息
            //false：激活状态（Started）可以实时收到消息，非激活状态（Stoped）无法实时收到消息，需等到Activity重新变成激活状态，方可收到消息
            .lifecycleObserverAlwaysActive(false)

        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
                //使用MediaPlayer解码
                .setPlayerFactory(AndroidMediaPlayerFactory.create())
                .build()
        );

        val defaultInfo = MMKV.defaultMMKV()
        if (!defaultInfo.getBoolean("init", false)) {
            Log.e("init", "安装 app 第一次初始化配置信息")
            defaultInfo.putBoolean("init", true)
            val config: String = FileHelper().getTxtContent(this, "config.txt")
            defaultInfo.encode("config", config)
        }
        //bug ly
        CrashReport.initCrashReport(this, "155e1b1332", false);
    }
}