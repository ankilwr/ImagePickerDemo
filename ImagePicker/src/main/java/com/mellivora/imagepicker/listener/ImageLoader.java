package com.mellivora.imagepicker.listener;

import android.content.Context;
import android.widget.ImageView;

import com.mellivora.imagepicker.data.MediaFile;

import java.io.Serializable;

/**
 * 开放图片加载接口

 * Date: 2018/8/30
 * Time: 下午11:07
 */
public interface ImageLoader extends Serializable {

    /**
     * 显示提示语
     */
    void showToast(Context context, String message);

    /**
     * 缩略图加载方案
     *
     * @param imageView
     * @param file
     */
    void loadImage(ImageView imageView, MediaFile file);

    /**
     * 大图加载方案
     *
     * @param imageView
     * @param file
     */
    void loadPreImage(ImageView imageView, MediaFile file);


    /**
     * 视频播放方案
     *
     * @param imageView
     * @param path
     */
//    void loadVideoPlay(ImageView imageView, String path);

    /**
     * 缓存管理
     */
    void clearMemoryCache(Context context);

}
