package com.mike.cn.controltab.tools

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mike.cn.controltab.model.MenuInfoModel
import com.tencent.mmkv.MMKV
import java.security.MessageDigest
import java.util.*

/**
 * 获取配置信息助手
 */
open class ConfigHelper {


    val defaultInfo = MMKV.defaultMMKV()

    final val TAG1 = "6916457002358";
    final val TAG2 = "xiaoMai";
    final val TAG3 = "zhiNeng";

    /**
     * 获取配置菜单列表
     */
    open fun getConfigMenuList(type: String): ArrayList<MenuInfoModel> {
        try {
            val gson = Gson()
            val string = defaultInfo.decodeString("config", "")
            val mList: ArrayList<MenuInfoModel> = gson.fromJson(
                string, object : TypeToken<ArrayList<MenuInfoModel?>?>() {}.type
            )
            if (mList.isEmpty())
                return arrayListOf()

            val returnList = ArrayList<MenuInfoModel>()
            for (s in mList) {
                if (s.type == type)
                    returnList.add(s)
            }
            return if (type.isEmpty()) mList else returnList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ArrayList()

    }

    /**
     * 获取配置菜单列表2
     */
    open fun getConfigFixedMenuList(type: String): ArrayList<MenuInfoModel> {
        try {
            val gson = Gson()
            val string = defaultInfo.decodeString("config2", "")
            val mList: ArrayList<MenuInfoModel> = gson.fromJson(
                string, object : TypeToken<ArrayList<MenuInfoModel?>?>() {}.type
            )
            if (mList.isEmpty())
                return arrayListOf()

            val returnList = ArrayList<MenuInfoModel>()
            for (s in mList) {
                if (s.type == type)
                    returnList.add(s)
            }
            return if (type.isEmpty()) mList else returnList
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
            if (up.id == upInfo.id) {
                up.name = upInfo.name
                up.image = upInfo.image
                up.code = upInfo.code
            }
        }
        val jsonArray = gson.toJson(list)
        defaultInfo.encode("config", jsonArray.toString())
    }

    /**
     * 更新配置固定菜单列表中的信息
     */
    fun upDataConfigFixedMenuList(upInfo: MenuInfoModel) {
        val gson = Gson()
        val list: ArrayList<MenuInfoModel> = gson.fromJson(
            defaultInfo.decodeString("config2", ""),
            object : TypeToken<ArrayList<MenuInfoModel?>?>() {}.type
        )
        for (up in list) {
            if (up.id == upInfo.id) {
                up.name = upInfo.name
                up.image = upInfo.image
                up.code = upInfo.code
            }
        }
        val jsonArray = gson.toJson(list)
        defaultInfo.encode("config2", jsonArray.toString())
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    open fun getUUID(): String? {
        var serial = ""
        val m_szDevIDShort =
            "71" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10 //12 位
        try {
            serial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial()
            } else {
                Build.SERIAL
            }
            //API>=9 使用serial号
            return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        } catch (exception: java.lang.Exception) {
            //serial需要一个初始化
            serial = TAG1 // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }


    /**
     * 获取用户所知的设备 ID
     */
    open fun getEnCode(): String? {
        var code = getUUID()
        val strList: List<String>
        if (code?.isNotEmpty() == true) {
            strList = code.split("-")
            if (strList.size > 2)
                code = strList[1] + strList[2]
        }
        return code
    }

    /**
     * 验证激活码
     */
    open fun verifyCode(code: String): Boolean? {
        var t: String = Base64Util.encode(TAG2 + getEnCode() + TAG3)
        try {
            // 创建 MessageDigest 对象并指定算法为 MD5
            val md: MessageDigest = MessageDigest.getInstance("MD5")
            // 将输入字符串转换为字节数组
            val inputBytes: ByteArray = t.toByteArray()
            // 计算 MD5 散列值
            val md5Bytes: ByteArray = md.digest(inputBytes)

            // 将字节数组转换为十六进制字符串
            val sb = StringBuilder()
            for (b in md5Bytes) {
                sb.append(String.format("%02x", b))
            }
            t = sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e("激活码", t)
        return t == code
    }

}