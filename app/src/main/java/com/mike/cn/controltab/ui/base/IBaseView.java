package com.mike.cn.controltab.ui.base;

import androidx.annotation.StringRes;

/**
 * @describe ：基础对象 接口
 */
public interface IBaseView {



    /**
     * 显示圆形进度对话框
     */
    void showLoadingDialog(@StringRes int resId);

    /**
     * 显示圆形进度对话框
     */
    void showLoadingDialog(String str);

    /**
     * 修改进度条的文本
     */
    void updateProgressDialogText(String str);

    /**
     * 修改进度条的文本
     */
    void updateProgressDialogText(@StringRes int str);

    /**
     * 显示圆形进度对话框（不可关闭）
     */
    void showNoCancelLoadingDialog(@StringRes int resId);

    /**
     * 显示圆形进度对话框（不可关闭）
     */
    void showNoCancelLoadingDialog(String str);

    /**
     * 关闭进度对话框
     */
    void dismissLoadingDialog();

}
