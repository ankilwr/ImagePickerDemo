package com.mellivora.imagepicker.listener;

import android.app.Activity;

import com.mellivora.imagepicker.data.MediaFile;
import com.mellivora.imagepicker.manager.ConfigManager;

import java.io.Serializable;

/**
 * 开放视频加载接口
 */
public interface VideoLoader extends Serializable {

    /**
     * 视频点击
     * @param context //上下文对象
     * @param videoFile 视频文件
     * @param config 当前图库的配置信息
     * @return Boolean 是否处理该事件: false交给图库处理
     */
    Boolean onVideoClick(Activity context, MediaFile videoFile, ConfigManager config);


}
