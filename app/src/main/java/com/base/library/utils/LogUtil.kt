package com.base.library.utils

import android.util.Log
import com.mellivora.demo.BuildConfig

object LogUtil {

    fun info(tag: String, msg: String) {
        var msgContent = msg
        val LOG_MAXLENGTH = 2048
//        if (true) {
        if (BuildConfig.DEBUG) {
            val length = msgContent.length.toLong()
            if (length < LOG_MAXLENGTH || length == LOG_MAXLENGTH.toLong()) {
                Log.i(tag, msgContent)
            } else {
                while (msgContent.length > LOG_MAXLENGTH) {
                    val logContent = msgContent.substring(0, LOG_MAXLENGTH)
                    msgContent = msgContent.replace(logContent, "")
                    Log.i(tag, logContent)
                }
                Log.i(tag, msgContent)
            }
        }
    }

    fun i(tag: String, msg: String?) {
//        if (true) Log.i(tag, msg ?: "null")
        if (BuildConfig.DEBUG) Log.i(tag, msg ?: "null")
    }

    fun e(tag: String, msg: String?) {
        if (BuildConfig.DEBUG) Log.e(tag, msg ?: "null")
    }

    fun w(tag: String, msg: String?) {
        if (BuildConfig.DEBUG) Log.w(tag, msg ?: "null")
    }

    fun d(tag: String, msg: String?) {
        if (BuildConfig.DEBUG) Log.d(tag, msg ?: "null")
    }

}
