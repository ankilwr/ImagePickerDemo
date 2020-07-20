package com.base.library.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast


object TShow {

    private var toast: Toast? = null

    @SuppressLint("ShowToast")
    private fun getToast(context: Context): Toast {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
        }
        return toast as Toast
    }

    fun show(context: Context, msg: CharSequence?, duration: Int = Toast.LENGTH_SHORT, gravity: Int = -1) {
        if(msg == null) return
        getToast(context).setText(msg)
        toast!!.duration = duration
        if (gravity != -1) toast!!.setGravity(gravity, 0, 0)
        toast!!.show()
    }

    fun show(context: Context, msg: Int, duration: Int = Toast.LENGTH_SHORT, gravity: Int = -1) {
        show(context, context.getString(msg), duration, gravity)
    }



}
