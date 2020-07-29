package com.mellivora.imagepicker.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

/**

 * Date: 2019/2/3
 * Time: 1:01 AM
 */
public class PermissionUtil {

    /**
     * 权限检查
     *
     * @param context
     * @return
     */
    public static boolean checkPermission(Context context) {
        boolean readStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean writeStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean camera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        return readStorage && writeStorage && camera;
    }
}
