package com.base.library.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.net.ConnectivityManager
import java.util.regex.Pattern

object CheckUtil {


    /**
     * (phoneNum is null return false)
     * yes return true; no return false;
     * @param phoneNum
     * @return boolean
     */
    fun isMobile(phoneNum: CharSequence?): Boolean {
        return if (phoneNum != null) {
            val p = Pattern.compile("^((13[0-9])|(14[5|7])|(15[0-9])|(17[0|7|8])|(18[0-9]))\\d{8}$")// \\d{8}代表后面还有8个数字,$表示结束
            p.matcher(phoneNum).matches()
        } else {
            false
        }
    }

    fun isPhone(phoneNum: CharSequence?): Boolean {
        return phoneNum?.length == 11
    }


    /**
     * (carCode is null return false)
     * yes return true; no return false;
     * @param carCode
     * @return boolean
     */
    fun isCarCode(carCode: String?): Boolean {
        if (carCode != null) {
            val p = Pattern.compile("^[\u4E00-\u9FA5][A-Za-z]{1}[A-Za-z_0-9]{5}$")//验证车牌号
            val m = p.matcher(carCode)
            return m.matches()
        } else {
            return false
        }
    }


    /**
     * (email is null return false)
     * yes return true; no return false;
     * @param email
     * @return boolean
     */
    fun isEmail(email: String): Boolean {
        val str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
        val p = Pattern.compile(str)
        val m = p.matcher(email)
        return m.matches()
    }


    /** true : net Connection;  false: net disconnect  */
    fun isConnected(context: Context): Boolean {
        val conn = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = conn.activeNetworkInfo
        return info != null && info.isConnected
    }


    /** true : s = number | Chinese | English  false: ,.*  */
    fun isValidTagAndAlias(s: String): Boolean {
        val p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$")
        val m = p.matcher(s)
        return m.matches()
    }


    /**
     * To show whether the String is null or is an empty String
     */
    fun isNullOrNil(str: String?): Boolean {
        return str == null || "" == str || str.isEmpty()
    }


    /** APP is not runing true:false   */
    fun isBackground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        appProcesses.filter { it.processName == context.packageName }
                .forEach { return it.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
        return false
    }
}
