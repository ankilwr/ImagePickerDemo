package com.base.library.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.*

object BitmapUtils {

    fun getFile(context: Context, uri: Uri): File? {
        try {
            val filePath = UriUtils.getPath(context, uri)
            return File(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getZoomSize(options: BitmapFactory.Options, needWidth: Int, needHeight: Int): Int {
        var zoomSize = 1
        if (options.outWidth > needWidth || options.outHeight > needHeight) {
            val widthZoom = Math.round((options.outWidth / needWidth).toFloat())
            val heightZoom = Math.round((options.outHeight / needHeight).toFloat())
            zoomSize = if (widthZoom < heightZoom) widthZoom else heightZoom
        }
        return zoomSize
    }

    /** 读取图片属性：旋转的角度  */
    fun getDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    /** 旋转图片  */
    fun rotateBitmap(angle: Int, bitmap: Bitmap): Bitmap {
        //旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun getThumbnail(filePath: String, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // 不把图片加载进内存也能计算出大小
        BitmapFactory.decodeFile(filePath, options)
        options.inSampleSize = getZoomSize(options, width, height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    fun saveBitmap(filePath: String, saveFile: File, width: Int, height: Int, quality: Int): Boolean {
        var bitmap = getThumbnail(filePath, width, height)
        val degree = getDegree(filePath)
        if (degree > 0) {
            bitmap = rotateBitmap(degree, bitmap)
        }
        try {
            return bitmap.compress(Bitmap.CompressFormat.JPEG, quality, FileOutputStream(saveFile))
        } catch (e: FileNotFoundException) {

        }
        return false
    }


    @Throws(IOException::class)
    fun bitmapToFile(bitmap: Bitmap, saveFile: File, qualite: Int) {
        val bos = BufferedOutputStream(FileOutputStream(saveFile))
        bitmap.compress(Bitmap.CompressFormat.JPEG, qualite, bos)
        bos.flush()
        bos.close()
    }


    @JvmOverloads
    fun compressBitmap(photo: String, goalPath: String, replace: Boolean = false, qualite: Int = 60): Boolean {
        val compressBitmap = File(goalPath)
        val result = saveBitmap(photo, compressBitmap, 720, 1280, qualite)
        if (result && replace) {
            val photoFile = File(photo)
            if (photoFile.exists()) photoFile.delete()
        }
        return result
    }

}