package com.mike.cn.controltab;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.mike.cn.controltab.service.MyJobIntentService;
import com.mike.cn.controltab.tools.SystemCtrlUtil;
import com.mike.cn.controltab.ui.activity.IndexActivity;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 启动后台任务
                Intent i = new Intent(context, IndexActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyJobIntentService.enqueueWork(context, i);
            } else {
                // 在 Android 8.0 以下版本，直接启动应用
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                context.startActivity(launchIntent);
            }
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.e("监听开机广播", "已开机");
            // 创建 PendingIntent 以触发启动应用的操作
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            // 发送 PendingIntent 来启动应用
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException ex) {
                ex.printStackTrace();
            }
            SystemCtrlUtil.resBootSlient();
        }
    }

}
