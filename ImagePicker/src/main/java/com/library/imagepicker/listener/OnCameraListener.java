package com.library.imagepicker.listener;

import android.app.Activity;

import com.library.imagepicker.manager.ConfigManager;

import java.io.Serializable;

public interface OnCameraListener {

    void openCamera(Activity context, int requestCode);

    void openVideoRecord(Activity context, int requestCode);

    void onVideoRecordSuccess(String path);

}
