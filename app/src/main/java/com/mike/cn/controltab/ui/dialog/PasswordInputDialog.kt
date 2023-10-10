package com.mike.cn.controltab.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.luck.picture.lib.utils.ToastUtils
import com.mike.cn.controltab.R
import com.mike.cn.controltab.app.ConnectConfig.PASS_TAG
import com.mike.cn.controltab.ui.activity.ExpertSettingActivity
import com.tencent.mmkv.MMKV

class PasswordInputDialog(context: Context, type: Int) {

    private val editTextPassword: EditText
    private val editTextPassword2: EditText
    private val cancel: View
    private val com: View

    init {
        // 创建一个包含密码输入框的布局
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.dialog_centre, null)
        layout.findViewById<TextView>(R.id.tvName).text = "验证密码"
        editTextPassword = layout.findViewById(R.id.et_password)
        editTextPassword2 = layout.findViewById(R.id.et_password2)
        cancel = layout.findViewById(R.id.tvCancel)
        com = layout.findViewById(R.id.tvMargin)
        if (type == 1) {
            editTextPassword2.visibility = View.VISIBLE
        }
        // 创建 AlertDialog
        val alertDialog = AlertDialog.Builder(context, R.style.DialogCentre)
            .setView(layout)
            .create()
        com.setOnClickListener {
            // 当用户点击 "OK" 按钮时执行的操作
            val enteredPassword = editTextPassword.text.toString()
            val enteredPassword2 = editTextPassword2.text.toString()
            if (type == 1) {
                if (enteredPassword2 != enteredPassword) {
                    ToastUtils.showToast(context, "两次密码不一致")
                } else {
                    MMKV.defaultMMKV().putString(PASS_TAG, enteredPassword)
                    alertDialog.dismiss()
                    ToastUtils.showToast(context, "设置成功")
                }

            } else {
                // 在这里处理用户输入的密码
                val pas = MMKV.defaultMMKV().getString(PASS_TAG, "123456")
                if (enteredPassword == pas) {
                    val intent = Intent(context, ExpertSettingActivity::class.java)
                    context.startActivity(intent)
                    alertDialog.dismiss()
                } else {
                    ToastUtils.showToast(context, "密码错误")

                }
            }
        }

        cancel.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
}
