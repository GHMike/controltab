package com.mike.cn.controltab.tools;

import android.util.Log;

import java.io.UnsupportedEncodingException;


/**
 * Base64加密解密工具类
 */
public class Base64Util {
    private static final String charset = "utf-8";

    /**
     * 解密
     *
     * @param data
     * @return -
     * @author jqlin
     */
    public static String decode(String data) {
        try {
            if (null == data) {
                return null;
            }

            return new String(Base64.decode(data.getBytes(charset)), charset);
        } catch (UnsupportedEncodingException e) {
            Log.e("Base64Util", (String.format("解密异常:%s", e)));
        }

        return null;
    }

    /**
     * 加密
     *
     * @param data
     * @return -
     * @author jqlin
     */
    public static String encode(String data) {
        try {
            if (null == data) {
                return null;
            }
            return new String(Base64.encode(data.getBytes(charset)), charset);
        } catch (UnsupportedEncodingException e) {
            Log.e("Base64Util", (String.format("加密异常:%s", e)));
        }

        return null;
    }

}