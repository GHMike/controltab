package com.mike.cn.controltab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import androidx.fragment.app.Fragment;

import razerdp.basepopup.BasePopupWindow;

public class MyPopupWindow extends BasePopupWindow {
    public MyPopupWindow(Context context) {
        super(context);
    }

    public MyPopupWindow(Context context, int width, int height) {
        super(context, width, height);
    }

    public MyPopupWindow(Fragment fragment) {
        super(fragment);
    }

    public MyPopupWindow(Fragment fragment, int width, int height) {
        super(fragment, width, height);
    }

    public MyPopupWindow(Dialog dialog) {
        super(dialog);
    }

    public MyPopupWindow(Dialog dialog, int width, int height) {
        super(dialog, width, height);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        Animation slideDownAnimation = new TranslateAnimation(0, 0, -100, 0);
        slideDownAnimation.setDuration(500);
        return slideDownAnimation;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        Animation slideDownAnimation = new TranslateAnimation(0, 0, 0, -100);
        slideDownAnimation.setDuration(300);
        return slideDownAnimation;
    }
}
