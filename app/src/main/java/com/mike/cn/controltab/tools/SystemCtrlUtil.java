package com.mike.cn.controltab.tools;

import java.io.DataOutputStream;

/**
 * Created by wujn on 2018/12/13.
 * Version : v1.0
 * Function: 系统级别的控制，包括root
 * <p>
 * 参考：https://blog.csdn.net/andywuchuanlong/article/details/44150317
 */
public class SystemCtrlUtil {
    private static final String TAG = "SystemCtrlUtil";

    public static void installSlient(String apk) {
        String cmd = "pm install -r  -i  com.fsl.vending --user 0 " + apk;
        Process process = null;
        DataOutputStream os = null;
        try {
            //静默安装需要root权限
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void resBootSlient() {
        String cmd = " am start -n com.mike.cn.controltab/com.mike.cn.controltab.ui.activity.IndexActivity ";
        Process process = null;
        DataOutputStream os = null;
        try {
            //静默安装需要root权限
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


}