package com.base.library.expansion

import java.lang.Long.parseLong
import java.text.SimpleDateFormat
import java.util.*

fun Long.format(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(format, Locale.CHINA).format(Date(this))
}

fun String.format(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(format, Locale.CHINA).format(Date(parseLong(this)))
}

fun Date.format(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(format, Locale.CHINA).format(this)
}

fun String.toDate(format: String = "yyyy-MM-dd HH:mm:ss"): Date? {
    return try {
        SimpleDateFormat(format, Locale.CHINA).parse(this)
    }catch (e: Exception){
        null
    }
}