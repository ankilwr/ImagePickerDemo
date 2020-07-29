package com.base.library.utils


import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import com.mellivora.demo.R
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.net.URLConnection


object FileUtils {

    private var sdCache = "柚米" //应用的sd卡缓存路径,最好取名为应用名
    var PHOTO = Environment.DIRECTORY_DCIM //照片存储目录
    var THUMB = "Thumb" //压缩照片存储目录
    var Temp = "Temp" //临时文件(用完即删)
    var FILES = "files" //接收的文件存储目录
    var Video = "video" //缓存视屏的路径
    var DownLoads = "DownLoads" //接收的文件存储目录

    private lateinit var context: Context

    fun getAliyunEncryptedFile(): File{
        val documents = context.getExternalFilesDir("AliYun")
        return File("$documents/encryptedApp.dat")
    }

    fun init(context: Context) {
        this.context = context.applicationContext
        sdCache = context.resources.getString(R.string.app_name)
    }


    fun hasSdCard(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }


    /** 创建一个文件 */
    fun createFile(path: String): File {
        val file = File(path)
        if (!file.exists()) file.createNewFile()
        return file
    }

    //获取SD缓存路径
    private fun getSdCache(): File {
        context.getExternalFilesDir("")
        val path = if (hasSdCard()) {
            Environment.getExternalStorageDirectory().toString() + File.separator + sdCache
        } else {
            Environment.getDataDirectory().toString() + File.separator + sdCache
        }
        val dir = File(path)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /** 获取SD卡缓存路径(No Delete)  */
    fun getSdCachePath(folderName: String): String {
        return getSdCache().path + File.separator + folderName
    }

    /** 获取应用缓存文件夹(No Delete)  */
    fun getSdCache(folder: String): File {
        val path = getSdCache().path + File.separator + folder
        val dir = File(path)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /**
     * 返回的File可能会不存在
     */
    fun getSdCacheFile(context: Context, folderName: String, fileName: String): File {
        val folderPath = getSdCache().path + File.separator + folderName
        val dir = File(folderPath)
        if (!dir.exists()) dir.mkdirs()
        val path = dir.path + File.separator + fileName
        return File(path)
    }


    fun getSdCacheFilePath(folderName: String, fileName: String): String {
        val folderPath = getSdCache().path + File.separator + folderName
        val dir = File(folderPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir.path + File.separator + fileName
    }


    /** 删除指定目录下文件及目录  (第二个参数为是否要删除该文件夹，false为清空文件夹但不删除)  */
    fun deleteFile(filePath: String, deleteThisDir: Boolean = true): Boolean {
        if (!TextUtils.isEmpty(filePath)) {
            return try {
                val file = File(filePath)
                if (file.isDirectory) {// 处理目录
                    file.listFiles().forEach { deleteFile(it.absolutePath, true) }
                    if(deleteThisDir) file.delete()
                }else{
                    file.delete()
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        }
        return true
    }


    fun getFile(context: Context, uri: Uri): File? {
        try {
            val filePath = UriUtils.getPath(context, uri)
            return File(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**  获取文件(夹)大小    */
    fun getFileSize(file: File): Long {
        var size: Long = 0
        try {
            if(file.isDirectory){
                file.listFiles().forEach {
                    size += if (it.isDirectory) getFileSize(it) else it.length()
                }
            }else{
                size = file.length()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //return size/1048576; MB
        return size
    }

    /** 获取文件大小(格式化单位)  */
    fun getFormatSize(file: File): String{
        return getFormatSize(getFileSize(file))
    }

    /** 格式化单位  */
    fun getFormatSize(fileSize: Long): String {
        var size = fileSize.toDouble()
        if (size < 1024) {
            return size.toString() + "Byte(s)"
        }
        size /= 1024.0
        if (size < 1024) {
            val result1 = BigDecimal(size.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }
        size /= 1024.0
        if (size < 1024) {
            val result2 = BigDecimal(size.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }
        size /= 1024.0
        if (size < 1024) {
            val result3 = BigDecimal(size.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(size)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }

    fun getPath(context: Context, uri: Uri): String? {
        var filePath: String? = null
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            filePath = uri.path
        }
        return filePath
    }

    fun getUri(context: Context, path: String): Uri{
        return if (Build.VERSION.SDK_INT < 24) {
            Uri.fromFile(File(path))
        } else {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", File(path))
        }
    }


    fun getMimeType(fileName: String): String {
        val fileNameMap = URLConnection.getFileNameMap()
        return fileNameMap.getContentTypeFor(fileName)
    }

    fun isVideoFile(fileName: String): Boolean {
        val mimeType = getMimeType(fileName)
        return !TextUtils.isEmpty(fileName) && mimeType.contains("video/")
    }

    fun isImageFile(fileName: String): Boolean {
        val mimeType = getMimeType(fileName)
        return !TextUtils.isEmpty(fileName) && mimeType.contains("image/")
    }

    fun isAudioFile(fileName: String): Boolean {
        val mimeType = getMimeType(fileName)
        return !TextUtils.isEmpty(fileName) && mimeType.contains("audio/")
    }



    fun copyAssetsToSd(context: Context, assetsName: String, sdPath: String){
        val inputStream = context.assets.open(assetsName)
        val outFile = File(sdPath)
        val fos = FileOutputStream(outFile)
        val buffer = ByteArray(1024)
        var byteCount: Int = inputStream.read(buffer)
        while (byteCount != -1) {
            fos.write(buffer, 0, byteCount)
            byteCount = inputStream.read(buffer)
        }
        fos.flush()
        inputStream.close()
        fos.close()
    }

    fun getResUri(context: Context, @DrawableRes resId: Int): Uri{
        val resources = context.resources
        return Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${resources.getResourcePackageName(resId)}/${resources.getResourceTypeName(resId)}/${resources.getResourceEntryName(resId)}")
    }


}
