package com.imagepicket

import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.glide.GlideApp
import com.library.imagepicker.data.MediaFile
import com.library.imagepicker.listener.ImageLoader

class ImagePickerLoader : ImageLoader {

    private val mOptions = RequestOptions()
            .centerCrop()
            .dontAnimate()
            .format(DecodeFormat.PREFER_RGB_565)


    private val mPreOptions = RequestOptions().skipMemoryCache(true)

    override fun showToast(context: Context, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    override fun loadImage(imageView: ImageView, file: MediaFile?) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            GlideApp.with(imageView).load(file?.uri).apply(mOptions).into(imageView)
        } else {
            GlideApp.with(imageView).load(file?.path).apply(mOptions).into(imageView)
        }
    }


    override fun loadPreImage(imageView: ImageView, file: MediaFile?) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            GlideApp.with(imageView).load(file?.uri).apply(mPreOptions).into(imageView)
        } else {
            GlideApp.with(imageView).load(file?.path).apply(mPreOptions).into(imageView)
        }
    }


    override fun clearMemoryCache(context: Context) {
//        GlideUtil.clearMemoryCache(context)
        GlideApp.get(context).clearMemory()
    }
}