package com.mike.cn.controltab.tools;

import android.app.Dialog;
import android.os.Build;
import android.view.View;

public class HideNavBarUtil {
    public static void hideBottomUIMenu(View v) {
        /**
         *隐藏虚拟按键，并且全屏// lower api
         */
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            v.setSystemUiVisibility(uiOptions);
        }

    }

    /**
     * 调起输入法 用于隐藏输入法后隐藏导航栏
     */

    public static void hideWhenSystemUiVisible(View v) {
        v.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                hideBottomUIMenu(v);
            }
        });
    }

    /**
     * 隐藏导航条
     *
     * @param dialog -
     */
    public static void hideNavigation(Dialog dialog) {
        // 全屏展示
        View decorView = dialog.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 隐藏导航栏
                | View.SYSTEM_UI_FLAG_FULLSCREEN // 隐藏状态栏
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
