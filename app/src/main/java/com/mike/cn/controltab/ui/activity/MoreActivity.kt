package com.mike.cn.controltab.ui.activity

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.ConfigHelper
import com.mike.cn.controltab.tools.UdpUtil
import com.mike.cn.controltab.ui.adapters.MenuAdapter
import com.mike.cn.controltab.ui.base.BaseActivity
import com.mike.cn.controltab.ui.dialog.CustomDialog
import com.tencent.mmkv.MMKV

/**
 * 更多模式
 */
@SuppressLint("NotifyDataSetChanged,UseSwitchCompatOrMaterialCode")
class MoreActivity : BaseActivity(), CustomDialog.OnButtonClickListener {

    private var rvDataView: RecyclerView? = null
    private var ivBack: View? = null

    private var moreAdapter: MenuAdapter? = null

    var dialog: CustomDialog? = null

    var isEdit = MMKV.defaultMMKV().getBoolean(ConnectConfig.IS_EDIT, false)

    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_more)
    }


    /**
     * 初始化页面控件
     */
    override fun initView() {
        rvDataView = findViewById(R.id.rv_data)
        ivBack = findViewById(R.id.iv_back)
        moreAdapter = MenuAdapter();
        rvDataView?.layoutManager = GridLayoutManager(context, 6)
        rvDataView?.addItemDecoration(DividerItemDecoration(this, 1))
        rvDataView?.adapter = moreAdapter
    }

    /**
     * 数据初始化
     */
    override fun obtainData() {
        val mainList = ConfigHelper().getConfigMenuList("more")
        for (i in mainList) {
            if (i.id == ConnectConfig.MORE_ID) {
                i.itemType = 1
            }
        }
        moreAdapter?.setList(mainList)
    }

    private fun showCustomDialog(data: MenuInfoModel) {
        if (dialog == null) {
            dialog = CustomDialog(context, data, this)
        } else {
            dialog?.setData(data)
        }
        dialog?.show()
    }

    override fun onPositiveButtonClick() {

    }

    override fun onNegativeButtonClick(infoModel: MenuInfoModel) {
        ConfigHelper().upDataConfigMenuList(infoModel)
        Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show()
        obtainData()
    }

    /**
     * 事件初始化
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun initEvent() {
        ivBack?.setOnClickListener {
            onBackPressed()
        }

        moreAdapter?.setOnItemLongClickListener { _, _, position ->
            if (isEdit)
                showCustomDialog(moreAdapter!!.getItem(position))
            true
        }
        moreAdapter?.setOnItemClickListener() { _, view, position ->
            UdpUtil.getInstance().sendUdpCommand(moreAdapter?.getItem(position)?.code)
            playRaw()
            // 缩放动画
            view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }).start()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // 释放MediaPlayer资源
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }


    override fun onResume() {
        super.onResume()
        isEdit = MMKV.defaultMMKV().getBoolean(ConnectConfig.IS_EDIT, false)
    }
}