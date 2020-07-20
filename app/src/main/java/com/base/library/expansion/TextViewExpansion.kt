package com.base.library.expansion


import android.graphics.Color
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat

@Suppress("NOTHING_TO_INLINE")
inline fun TextView.isEmpty(): Boolean = text.isNullOrEmpty()

fun TextView.textEquals(tv: TextView): Boolean {
    return this.text.toString() == tv.text.toString()
}

fun TextView.setBold(enable: Boolean): TextView? {
    this.paint.isFakeBoldText = enable
    //typeface = Typeface.defaultFromStyle(if (enable) Typeface.BOLD else Typeface.NORMAL)
    invalidate()
    return this
}

fun TextView.strikeLine(): TextView? {
    this.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
    this.paint.isAntiAlias = true
    return this
}

fun TextView.underLine(): TextView? {
    this.paint.flags = Paint.UNDERLINE_TEXT_FLAG
    this.paint.isAntiAlias = true
    return this
}




fun TextView.matchingSize(matchingStr: String?, @DimenRes size: Int): TextView {
    if (text.isEmpty() || matchingStr.isNullOrEmpty()) {
        return this
    }
    val builder = SpannableStringBuilder(text)
    val textSpan = AbsoluteSizeSpan(this.textSize.toInt(), false)
    builder.setSpan(textSpan, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    var indexStart = text.indexOf(matchingStr)
    while (indexStart != -1) {
        val indexEnd = indexStart + matchingStr.length
        val smallSpan = AbsoluteSizeSpan(context.resources.getDimensionPixelSize(size), false)
        builder.setSpan(smallSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        indexStart = text.indexOf(matchingStr, indexEnd)
    }
    this.text = builder
    return this
}

fun TextView.matchingColor(matchingStr: String?, @ColorInt matchingColor: Int): TextView {
    if (text.isEmpty() || matchingStr.isNullOrEmpty()) {
        return this
    }
    val builder = SpannableStringBuilder(text)
    var indexStart = text.indexOf(matchingStr)
    while (indexStart != -1) {
        val indexEnd = indexStart + matchingStr.length
        val colorSpan = ForegroundColorSpan(matchingColor)
        builder.setSpan(colorSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        indexStart = text.indexOf(matchingStr, indexEnd)
    }
    this.text = builder
    return this
}

fun TextView.matchingColorAndSize(text: String?, matchingStr: String?, @ColorInt matchingColor: Int, @DimenRes size: Int, reversed: Boolean = false) {
    if (text.isNullOrEmpty() || matchingStr.isNullOrEmpty()) {
        return
    }
    val builder = SpannableStringBuilder(text)

    if (reversed) {
        val colorSpan = ForegroundColorSpan(matchingColor)
        val textSpan = AbsoluteSizeSpan(context.resources.getDimensionPixelSize(size), false)
        builder.setSpan(colorSpan, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(textSpan, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    } else {
        val textSpan = AbsoluteSizeSpan(textSize.toInt(), false)
        builder.setSpan(textSpan, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }


    var indexStart = text.indexOf(matchingStr)
    while (indexStart != -1) {
        val indexEnd = indexStart + matchingStr.length

        val colorSpan = ForegroundColorSpan(if (reversed) textColors.defaultColor else matchingColor)
        val sizeSpan = AbsoluteSizeSpan(if (reversed) textSize.toInt() else context.resources.getDimensionPixelSize(size), false)
        builder.setSpan(colorSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(sizeSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        indexStart = text.indexOf(matchingStr, indexEnd)
    }
    this.text = builder
}


fun TextView.setSizeText(text: CharSequence?, matchingStr: String?, @DimenRes size: Int) {
    this.text = text
    if (text.isNullOrEmpty() || matchingStr.isNullOrEmpty()) return
    var indexStart = text.indexOf(matchingStr)
    val builder = SpannableStringBuilder(text)
    while (indexStart != -1) {
        val indexEnd = indexStart + matchingStr.length
        val sizeSpan = AbsoluteSizeSpan(context.resources.getDimensionPixelSize(size), false)
        builder.setSpan(sizeSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        indexStart = text.indexOf(matchingStr, indexEnd)
    }
    this.text = builder
}

fun TextView.setColorText(start: Int, end: Int, @ColorRes color: Int) {
    val builder = SpannableStringBuilder(this.text)
    val oneSpan = ForegroundColorSpan(ContextCompat.getColor(this.context, color))
    builder.setSpan(oneSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = builder
}

fun TextView.setColorText(str: CharSequence, start: Int, end: Int, @ColorRes color: Int) {
    val builder = SpannableStringBuilder(str.toString())
    val oneSpan = ForegroundColorSpan(ContextCompat.getColor(this.context, color))
    builder.setSpan(oneSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = builder
}

fun TextView.setColorText(text: CharSequence?, matchingStr: String?, @ColorInt matchingColor: Int): TextView {
    if (text.isNullOrEmpty() || matchingStr.isNullOrEmpty()) {
        this.text = text
        return this
    }
    val builder = SpannableStringBuilder(text)
    var indexStart = text.indexOf(matchingStr)
    while (indexStart != -1) {
        val indexEnd = indexStart + matchingStr.length
        val colorSpan = ForegroundColorSpan(matchingColor)
        builder.setSpan(colorSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        indexStart = text.indexOf(matchingStr, indexEnd)
    }
    this.text = builder
    return this
}



fun TextView.appendSizeText(appendContent: String?, @DimenRes size: Int): TextView {
    if (appendContent.isNullOrEmpty()) return this
    val builder = SpannableStringBuilder(appendContent)
    val sizeSpan = AbsoluteSizeSpan(context.resources.getDimensionPixelSize(size), false)
    builder.setSpan(sizeSpan, 0, appendContent.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(builder)
    return this
}


fun TextView.appendColorText(appendContent: String?, color: Int): TextView {
    if (appendContent.isNullOrEmpty()) return this
    val builder = SpannableStringBuilder(appendContent)
    val oneSpan = ForegroundColorSpan(color)
    builder.setSpan(oneSpan, 0, appendContent.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(builder)
    return this
}


fun TextView.appendColorAndSize(appendContent: CharSequence?, @ColorInt color: Int, @DimenRes size: Int): TextView {
    if (appendContent.isNullOrEmpty()) return this
    val builder = SpannableStringBuilder(appendContent)
    val colorSpan = ForegroundColorSpan(color)
    val textSpan = AbsoluteSizeSpan(context.resources.getDimensionPixelSize(size), false)
    builder.setSpan(colorSpan, 0, appendContent.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    builder.setSpan(textSpan, 0, appendContent.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(builder)
    return this
}



fun TextView.setMoneyStr(money: String?, @DimenRes priceSize: Int, reverse: Boolean = false, startStr: String = "¥", endStr: String = ".") {
    this.text = money
    if (money.isNullOrEmpty()) return
    var indexStart = text.indexOf(startStr)
    var indexEnd = text.indexOf(endStr)
    indexStart = if (indexStart == -1) 0 else indexStart + 1
    if (indexEnd == -1) indexEnd = money.length

    val builder = SpannableStringBuilder(text)
    val sizeSpan = AbsoluteSizeSpan(this.context.resources.getDimensionPixelSize(priceSize), false)
    val textSpan = AbsoluteSizeSpan(this.textSize.toInt(), false)
    if(reverse){
        builder.setSpan(sizeSpan, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(textSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }else{
        builder.setSpan(textSpan, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(sizeSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    this.text = builder
}
