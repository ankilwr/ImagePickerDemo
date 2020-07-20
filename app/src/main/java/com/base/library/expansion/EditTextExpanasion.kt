package com.base.library.expansion

import android.content.Context
import androidx.core.content.ContextCompat
import android.text.*
import android.text.method.DigitsKeyListener
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView


fun EditText.editble(enable: Boolean) {
    isEnabled = enable
    if (enable) {
        this.customSelectionActionModeCallback = null
    } else {
        this.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return false
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
            }
        }
    }
}

fun EditText.hideSoftInputFromWindow() {
    (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(this.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun EditText.showSoftInputFromWindow() {
    (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun EditText.perpetualEnd(unit: String, color: Int) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            var lastStr = s.toString()
            if (lastStr == unit) {
                setText(""); return
            }
            val spans = text.getSpans(0, s.length, ForegroundColorSpan::class.java)
            spans.forEach {
                val spanStart = text.getSpanStart(it)
                val spanEnd = text.getSpanEnd(it)
                if (start in spanStart..spanEnd) {
                    setText(s.substring(0, spanStart))
                    return
                }
            }
            val unitStart = lastStr.lastIndexOf(unit)
            if (lastStr.isNotEmpty() && unitStart == -1) {
                lastStr = "$lastStr$unit"
                val builder = SpannableStringBuilder(lastStr)
                val oneSpan = ForegroundColorSpan(ContextCompat.getColor(this@perpetualEnd.context, color))
                builder.setSpan(oneSpan, lastStr.length - unit.length, lastStr.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                text = builder
                setSelection(text.length - unit.length)
            }
        }
    })
}

fun EditText.lengthLinkage(lengthShowView: TextView, format: String) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            lengthShowView.text = String.format(format, s.length)
        }
    })
}

fun EditText.clearSpan(): String {
    val spans = text.getSpans(0, text.length, ForegroundColorSpan::class.java)
    val newText = SpannableStringBuilder(text)
    newText.clearSpans()
    spans.forEach {
        val spanStart = text.getSpanStart(it)
        val spanEnd = text.getSpanEnd(it)
        newText.replace(spanStart, spanEnd, "")
    }
    return newText.toString()
}

fun EditText.moneyStyle() {
    this.keyListener = DigitsKeyListener.getInstance("0123456789.")
    this.addTextChangedListener(object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            var changeStr = s.toString()
            if (changeStr.contains(".")) {
                if (changeStr.substring(0, changeStr.lastIndexOf(".")).contains(".")) {
                    //(有多个小数点)去除第二个点,setText之后会重新检测
                    changeStr = changeStr.substring(0, changeStr.length - 1)
                    setText(changeStr)
                    setSelection(changeStr.length)
                    return
                } else if (changeStr.length - 1 - changeStr.indexOf(".") > 2) {
                    //小数过多，截取前面2个小数
                    changeStr = changeStr.substring(0, changeStr.indexOf(".") + 3)
                    setText(changeStr)
                    setSelection(changeStr.length)
                    return
                }
            }
            //处理第一个字符为.的情况
            if (changeStr.trim { it <= ' ' }.substring(0) == ".") {
                changeStr = "0$changeStr"
                setText(changeStr)
                setSelection(2)
                return
            }
            //第一个字符为0的时候后面只能追加.(输入其他数值重置为0)
            if (changeStr.startsWith("0") && changeStr.trim { it <= ' ' }.length > 1 && changeStr.substring(1, 2) != ".") {
                setText(changeStr.subSequence(0, 1))
                setSelection(1)
                return
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun afterTextChanged(s: Editable) {

        }
    })
    this.setLastLength()
}

fun EditText.passVisible(isVisible: Boolean) {
    this.transformationMethod = if (isVisible) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
    this.setLastLength()
}

fun EditText.setLastLength() {
    this.postInvalidate()
    Selection.setSelection(this.text, this.text.length)
}
