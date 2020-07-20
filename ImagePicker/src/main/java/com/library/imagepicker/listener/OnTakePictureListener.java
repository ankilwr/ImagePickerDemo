package com.library.imagepicker.listener;

import android.app.Activity;
import android.content.Intent;

import com.library.imagepicker.ImagePicker.Picker;

import java.io.Serializable;

/**
 * 拍照按钮的点击监听
 */
public interface OnTakePictureListener extends Serializable {

    void onTakePicture(Activity context, @Picker String pickerType, OnCameraListener listener, int cameraCode, int videoCode);

    /**
     * 处理自定义录制结果
     */
    void onVideoRecordSuccess(Intent data, OnCameraListener listener);

}
