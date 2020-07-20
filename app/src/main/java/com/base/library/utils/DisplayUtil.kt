package com.base.library.utils

import android.app.Activity
import android.content.res.Resources
import android.util.DisplayMetrics

object DisplayUtil {

    fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dp(pxValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun getWindowWidth(context: Activity): Int {
        val dm = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    fun getWindowHeigth(context: Activity): Int {
        val dm = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }

}
