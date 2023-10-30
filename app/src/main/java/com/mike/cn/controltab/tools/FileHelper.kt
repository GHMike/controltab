package com.mike.cn.controltab.tools

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.mike.cn.controltab.R
import java.io.*


open class FileHelper {
    /**
     * 读取asset中txt文本内容
     * @param txtName 文件名，带后缀 例： Test.txt
     * @return 读取内容
     */
    open fun getTxtContent(context: Context, txtName: String): String {
        var fileContent = ""
        try {
            val inputStream: InputStream = context.getAssets().open(txtName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            fileContent = stringBuilder.toString()

            // 处理文件内容
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fileContent

    }


    /**
     * 根据文件名字获取Mipmap目录下的资源 id
     */
    open fun getMipmapResId(context: Context, name: String): Int {
        return if (name.isNotEmpty()) {
            // 获取资源标识符
            val resourceId = context.resources.getIdentifier(name, "mipmap", context.packageName)

            // 检查资源是否存在
            if (resourceId != 0) {
                resourceId;
            } else {
                R.mipmap.ic_launcher_round;
            }
        } else {
            R.mipmap.ic_launcher_round;
        }
    }

    open fun copyAssetToInternalStorage(context: Context, filename: String) {
        val assetManager: AssetManager = context.assets
        var `in`: InputStream? = null
        var out: FileOutputStream? = null
        try {
            `in` = assetManager.open(filename)
            val outFile = File(context.filesDir, filename)
            out = FileOutputStream(outFile)
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            Log.d("VideoPlayerActivity", "Copied $filename to internal storage")
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    open fun getInternalStoragePath(context: Context, filename: String): String {
        return File(context.filesDir, filename).absolutePath
    }
}