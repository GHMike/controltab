package com.mike.cn.controltab.ui.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig
import com.mike.cn.controltab.app.ConnectConfig.MORE_ID
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.ConfigHelper
import com.mike.cn.controltab.tools.UdpUtil
import com.mike.cn.controltab.ui.activity.MainActivity
import com.mike.cn.controltab.ui.adapters.MenuAdapter
import com.mike.cn.controltab.ui.dialog.CustomDialog
import com.tencent.mmkv.MMKV

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 场景控制
 */
class Tab2Fragment : Fragment(), CustomDialog.OnButtonClickListener {


    private var param1: String? = null
    private var param2: String? = null

    var rvData: RecyclerView? = null
    var myAdapter: MenuAdapter? = null
    var dialog: CustomDialog? = null

    var isEdit = MMKV.defaultMMKV().getBoolean(ConnectConfig.IS_EDIT, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_tab2, container, false)
        initW(v)
        return v
    }

    private fun initW(v: View) {
        rvData = v.findViewById(R.id.rv_data)

        val glm = GridLayoutManager(context, 6)

        //count/size=item数量
        glm.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 18) {
                    6 //size
                } else {
                    1
                }
            }
        }

        rvData?.layoutManager = glm
        myAdapter = MenuAdapter()
        rvData?.adapter = myAdapter
        initData()

        myAdapter?.setOnItemLongClickListener { _, _, position ->
            if (isEdit)
                showCustomDialog(myAdapter!!.getItem(position))
            true
        }
        myAdapter?.setOnItemClickListener() { _, view, position ->
            getMainActivity()?.playRaw()
            // 缩放动画
            view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(Runnable {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }).start()
            if (myAdapter?.getItem(position)?.id == MORE_ID) {
//                val intent = Intent(context, MoreActivity::class.java)
//                startActivity(intent)
                UdpUtil.getInstance().sendUdpCommand(myAdapter?.getItem(position)?.code)
                getMainActivity()?.goFragment(MoreFragment.newInstance("", ""), true)
            } else {
                UdpUtil.getInstance().sendUdpCommand(myAdapter?.getItem(position)?.code)
            }
        }

    }

    /**
     * 返回主页对象
     *
     * @return
     */
    fun getMainActivity(): MainActivity? {
        return if (activity is MainActivity) {
            activity as MainActivity?
        } else {
            null
        }
    }

    private fun initData() {
        val mainList = ConfigHelper().getConfigMenuList("2")
        for (i in mainList) {
            if (i.id == MORE_ID) {
                i.itemType = 1
            }
        }
        myAdapter?.setList(mainList)
    }

    private fun showCustomDialog(data: MenuInfoModel) {
        if (dialog == null) {
            dialog = CustomDialog(activity, data, this)
        } else {
            dialog?.setData(data)
        }
        dialog?.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Tab2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onPositiveButtonClick() {

    }

    override fun onNegativeButtonClick(infoModel: MenuInfoModel) {
        ConfigHelper().upDataConfigMenuList(infoModel)
        Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放MediaPlayer资源
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        isEdit = MMKV.defaultMMKV().getBoolean(ConnectConfig.IS_EDIT, false)
    }
}