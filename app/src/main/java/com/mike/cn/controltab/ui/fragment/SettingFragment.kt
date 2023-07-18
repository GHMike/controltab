package com.mike.cn.controltab.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mike.cn.controltab.R
import com.mike.cn.controltab.ui.activity.PortSetActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SettingFragment : Fragment(), View.OnClickListener {
    private var param1: String? = null
    private var param2: String? = null

    var but1: TextView? = null
    var but2: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        initData()
    }

    fun initW(con: View) {
        but1 = con.findViewById(R.id.but1)
        but2 = con.findViewById(R.id.but2)

        but1?.setOnClickListener(this)
        but2?.setOnClickListener(this)
    }


    fun initData() {

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.fragment_setting, container, false)
        initW(v)
        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.but1 ->
                Toast.makeText(context, "", Toast.LENGTH_LONG).show()
            R.id.but2 -> {
                Toast.makeText(context, "2", Toast.LENGTH_LONG).show()
                val intent = Intent(context, PortSetActivity::class.java)
                context?.startActivity(intent)
            }

        }
    }
}