package com.glide

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.example.androidqtest.R


fun GlideRequests.loadDefault(path: String?, imageView: ImageView, placeImage: Int = R.drawable.default_image) {
    loadDefault(path, placeImage).into(imageView)
}

fun GlideRequests.loadDefault(uri: Uri?, imageView: ImageView, placeImage: Int = R.drawable.default_image) {
    loadDefault(uri, placeImage).into(imageView)
}

fun GlideRequests.loadDefault(path: String?, placeImage: Int = R.drawable.default_image): GlideRequest<Drawable> {
    return this.load(path).placeholder(placeImage)
}

fun GlideRequests.loadDefault(uri: Uri?, placeImage: Int = R.drawable.default_image): GlideRequest<Drawable> {
    return this.load(uri).placeholder(placeImage)
}
