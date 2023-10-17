package com.mike.cn.controltab.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService

class MyJobIntentService : JobIntentService() {

    companion object {
        private const val JOB_ID = 1001

        @JvmStatic
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, MyJobIntentService::class.java, JOB_ID, work)
        }

    }

    override fun onHandleWork(intent: Intent) {
        // 在这里执行需要在后台运行的任务
        // ...
    }
}
