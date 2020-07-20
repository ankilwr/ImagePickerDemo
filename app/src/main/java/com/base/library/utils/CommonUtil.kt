package com.base.library.utils

import android.widget.TextView
import com.base.library.expansion.appendColorText
import java.lang.Long.parseLong
import java.text.SimpleDateFormat
import java.util.*

object CommonUtil {


    fun time(timestamp: Long, format: String = "yyyy-MM-dd HH:mm:ss"): String {
        return SimpleDateFormat(format, Locale.CHINA).format(Date(timestamp))
    }

    fun time(timestamp: String, format: String = "yyyy-MM-dd HH:mm:ss"): String? {
        return SimpleDateFormat(format, Locale.CHINA).format(Date(parseLong(timestamp)))
    }

    fun time(date: Date, format: String = "yyyy-MM-dd HH:mm:ss"): String? {
        return SimpleDateFormat(format, Locale.CHINA).format(date)
    }

    /** 将格式化好的日期转换成 Date类型
     * @param date: 字符串类型  1900-12-12 15:30:20
     * @param format: 字符串格式化公式  yyyy-MM-dd HH:mm:ss（对应date的格式）
     * @return Date
     */
    fun parseDate(date: String?, format: String = "yyyy-MM-dd HH:mm:ss"): Date? {
        if(date == null) return null
        return try {
            SimpleDateFormat(format, Locale.CHINA).parse(date)
        }catch (e: Exception){
            null
        }
    }



    fun appendStar(vararg textView: TextView, colorRes: Int = android.R.color.holo_red_light) {
        textView.iterator().forEach { it.appendColorText("*", colorRes) }
    }



}
