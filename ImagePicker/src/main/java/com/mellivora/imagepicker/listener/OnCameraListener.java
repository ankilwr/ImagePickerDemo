package com.mellivora.imagepicker.listener;

import android.app.Activity;

public interface OnCameraListener {

    void openCamera(Activity context, int requestCode);

    void openVideoRecord(Activity context, int requestCode);

    void onVideoRecordSuccess(String path);

}
