package com.mike.cn.controltab.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mike.cn.controltab.R
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.ConfigHelper
import com.mike.cn.controltab.ui.adapters.MenuAdapter
import com.mike.cn.controltab.ui.dialog.CustomDialog

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

    fun initW(v: View) {
        rvData = v.findViewById(R.id.rv_data)
        rvData?.layoutManager = GridLayoutManager(context, 6)
        myAdapter = MenuAdapter()
        rvData?.adapter = myAdapter
        initData()

        myAdapter?.setOnItemLongClickListener { _, _, position ->
            showCustomDialog(myAdapter!!.getItem(position))
            true
        }


    }

    fun initData() {
        myAdapter?.setList(ConfigHelper().getConfigMenuList("2"))
    }

    private fun showCustomDialog(data: MenuInfoModel) {
        val dialog = CustomDialog(activity, data, this)
        dialog.show()
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

}