package com.mike.cn.controltab.ui.activity

import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mike.cn.controltab.R
import com.mike.cn.controltab.ui.base.BaseActivity
import com.mike.cn.controltab.ui.fragment.*
import java.util.*

class MainActivity : BaseActivity() {


    var rgMenu: RadioGroup? = null
    private var indexFragment: Fragment? = null
    var tab1: Stack<Fragment>? = null
    var tab2: Stack<Fragment>? = null
    var tab3: Stack<Fragment>? = null
    var tab4: Stack<Fragment>? = null
    var tab5: Stack<Fragment>? = null


    override fun setContentLayout() {
        hideStatusBar()
        setContentView(R.layout.activity_main)
    }

    override fun initView() {
        rgMenu = findViewById(R.id.rg_menu)

        tab1 = Stack()
        tab2 = Stack()
        tab3 = Stack()
        tab4 = Stack()
        tab5 = Stack()
    }

    override fun obtainData() {
    }

    override fun initEvent() {
        rgMenu?.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb1 ->
                    displayFragment(Tab1Fragment.newInstance("", ""), true)
                R.id.rb2 -> {
                    displayFragment(Tab2Fragment.newInstance("", ""), true)
                }
                R.id.rb3 ->
                    displayFragment(Tab3Fragment.newInstance("", ""), true)
                R.id.rb4 ->
                    displayFragment(Tab4Fragment.newInstance("", ""), true)
                R.id.rb5 ->
                    displayFragment(SettingFragment.newInstance("", ""), true)
            }
        }
        rgMenu?.check(R.id.rb1)
    }


    /**
     * 显示fragment
     *
     * @param fragment
     * @param isAddToStack 是否加入到fragment栈里,用于返回
     */
    fun displayFragment(fragment: Fragment?, isAddToStack: Boolean) {
        if (fragment == null) return
        if (isAddToStack) {
            when (rgMenu?.getCheckedRadioButtonId()) {
                R.id.rb1 -> tab1?.push(fragment)
                R.id.rb2 -> tab2?.push(fragment)
                R.id.rb3 -> tab3?.push(fragment)
                R.id.rb4 -> tab4?.push(fragment)
                R.id.rb5 -> tab5?.push(fragment)
            }
        }
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        //第一次加载
        if (indexFragment == null) {
            ft.add(R.id.fl_content, fragment, fragment.javaClass.simpleName)
        } else if (indexFragment !== fragment && !fragment.isAdded) {
            ft.hide(indexFragment!!).add(R.id.fl_content, fragment, fragment.javaClass.simpleName)
        } else if (indexFragment !== fragment) {
            ft.hide(indexFragment!!).show(fragment)
        }
        indexFragment = fragment
        ft.commitAllowingStateLoss()
    }
}