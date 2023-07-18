package com.mike.cn.controltab.ui.base;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.mike.cn.controltab.app.MyApp;
import com.mike.cn.controltab.tools.HideNavBarUtil;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @describe ：基础activity
 */
public abstract class BaseActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        init();
        setContentLayout();//由具体的activity实现，设置内容布局ID
        initView();//由具体的activity实现，做视图相关的初始化
        obtainData();//由具体的activity实现，做数据的初始化
        initEvent();//由具体的activity实现，做事件监听的初始化
        HideNavBarUtil.hideWhenSystemUiVisible(this.getWindow().getDecorView());
        HideNavBarUtil.hideBottomUIMenu(this.getWindow().getDecorView());
    }


    private void init() {
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 隐藏标题栏
     */
    public void hideStatusBar() {
        getWindow().setStatusBarColor(Color.BLACK);
        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public Context getContext() {
        return this;
    }


    public int getResourceColor(@ColorRes int colorId) {
        return ResourcesCompat.getColor(getResources(), colorId, null);
    }


    public String getResourceString(@StringRes int stringId) {
        return getResources().getString(stringId);
    }


    public String getResourceString(@StringRes int id, Object... formatArgs) {
        return getResources().getString(id, formatArgs);
    }


    public Drawable getResourceDrawable(@DrawableRes int id) {
        return ResourcesCompat.getDrawable(getResources(), id, null);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mOnKeyClickListener != null) {//如果没有设置返回事件的监听，则默认finish页面。
                    mOnKeyClickListener.clickBack();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    OnKeyClickListener mOnKeyClickListener;

    public void setOnKeyListener(OnKeyClickListener onKeyClickListener) {
        this.mOnKeyClickListener = onKeyClickListener;
    }

    /**
     * 按键的监听，供页面设置自定义的按键行为
     */
    public interface OnKeyClickListener {
        /**
         * 点击了返回键
         */
        void clickBack();

        //可加入其它按键事件
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


}
