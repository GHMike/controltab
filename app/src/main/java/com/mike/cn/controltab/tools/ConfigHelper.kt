package com.mike.cn.controltab.tools

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mike.cn.controltab.model.MenuInfoModel
import com.tencent.mmkv.MMKV

/**
 * 获取配置信息助手
 */
open class ConfigHelper {


    val defaultInfo = MMKV.defaultMMKV()

    /**
     * 获取配置菜单列表
     */
    open fun getConfigMenuList(): ArrayList<MenuInfoModel> {
        try {
            val gson = Gson()
            val string = defaultInfo.decodeString("config", "")
            val dbVer: ArrayList<MenuInfoModel> = gson.fromJson(
                string, object : TypeToken<ArrayList<MenuInfoModel?>?>() {}.type
            )
            if (dbVer.isNullOrEmpty())
                return arrayListOf()
            return dbVer
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ArrayList()

    }


    /**
     * 更新配置菜单列表中的信息
     */
    fun upDataConfigMenuList(upInfo: MenuInfoModel) {
        val gson = Gson()
        val list: ArrayList<MenuInfoModel> = gson.fromJson(
            defaultInfo.decodeString("config", ""),
            object : TypeToken<ArrayList<MenuInfoModel?>?>() {}.type
        )
        for (up in list) {
            if (up.code == upInfo.code) {
                up.name = upInfo.name
                up.image = upInfo.image
            }
        }
        val jsonArray = gson.toJson(list)
        defaultInfo.encode("config", jsonArray.toString())
    }
}