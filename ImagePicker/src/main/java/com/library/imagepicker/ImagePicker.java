package com.library.imagepicker;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.StringDef;

import com.library.imagepicker.activity.ImagePickerActivity;
import com.library.imagepicker.data.MediaFile;
import com.library.imagepicker.manager.ConfigManager;
import com.library.imagepicker.listener.ImageLoader;
import com.library.imagepicker.listener.OnTakePictureListener;
import com.library.imagepicker.listener.VideoLoader;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * 统一调用入口

 * Date: 2018/8/26
 * Time: 下午6:31
 */
public class ImagePicker {

    public static final String EXTRA_SELECT_IMAGES = "selectItems";
    public static final String EXTRA_ORIGINAL = "original";

    private static volatile ImagePicker mImagePicker;


    public static final String IMAGE = "image/*";
    public static final String VIDEO = "video/*";
    public static final String ALL = "*/*";

    @StringDef({ALL, IMAGE, VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Picker {}


    private ImagePicker() {
    }

    /**
     * 创建对象
     * @return
     */
    public static ImagePicker getInstance() {
        if (mImagePicker == null) {
            synchronized (ImagePicker.class) {
                if (mImagePicker == null) {
                    mImagePicker = new ImagePicker();
                }
            }
        }
        return mImagePicker;
    }


    /**
     * 设置标题
     * @param title
     * @return
     */
    public ImagePicker setTitle(String title) {
        ConfigManager.getInstance().setTitle(title);
        return mImagePicker;
    }


    /**
     * 是否开启原图模式
     * @param enable
     * @return
     */
    public ImagePicker setOriginalEnable(boolean enable){
        ConfigManager.getInstance().setOriginalEnable(enable);
        return mImagePicker;
    }

    /**
     * 是否支持相机
     * @param showCamera
     * @return
     */
    public ImagePicker showCamera(boolean showCamera) {
        ConfigManager.getInstance().setShowCamera(showCamera);
        return mImagePicker;
    }

    /**
     * 选择类型
     * @param type 选择类型
     * @return
     */
    public ImagePicker setPickerType(@Picker String type) {
        ConfigManager.getInstance().setPickerType(type);
        return mImagePicker;
    }


    /**
     * 设置图片是否单选
     * @param enable
     * @return
     */
    public ImagePicker setImageSingleEnable(boolean enable) {
        ConfigManager.getInstance().setImageSingleEnable(enable);
        return mImagePicker;
    }


    /**
     * 设置视屏是否单选
     * @param enable
     * @return
     */
    public ImagePicker setVideoSingleEnable(boolean enable) {
        ConfigManager.getInstance().setVideoSingleEnable(enable);
        return mImagePicker;
    }


    /**
     * 设置单类型选择（只能选图片或者视频）
     * @param isSingleType
     * @return
     */
    public ImagePicker setSingleType(boolean isSingleType) {
        ConfigManager.getInstance().setSingleType(isSingleType);
        return mImagePicker;
    }



    /**
     * 图片最大选择数
     * @param maxCount
     * @return
     */
    public ImagePicker setMaxCount(int maxCount) {
        ConfigManager.getInstance().setMaxCount(maxCount);
        return mImagePicker;
    }



    /**
     * 设置图片加载器
     * @param imageLoader
     * @return
     */
    public ImagePicker setImageLoader(ImageLoader imageLoader) {
        ConfigManager.getInstance().setImageLoader(imageLoader);
        return mImagePicker;
    }


    /**
     * 设置自定义视频处理逻辑
     * @param loader
     * @return
     */
    public ImagePicker setVideoLoader(VideoLoader loader) {
        ConfigManager.getInstance().setVideoLoader(loader);
        return mImagePicker;
    }

    /**
     * 设置拍照按钮点击监听
     * @param listener
     * @return
     */
    public ImagePicker setOnTakePictureListener(OnTakePictureListener listener) {
        ConfigManager.getInstance().setOnTakePictureListener(listener);
        return mImagePicker;
    }


    /**
     * 设置图片选择历史记录
     * @param imagePaths
     * @return
     */
    public ImagePicker setImagePaths(ArrayList<MediaFile> imagePaths) {
        ConfigManager.getInstance().setImagePaths(imagePaths);
        return mImagePicker;
    }

    /**
     * 启动
     * @param activity
     */
    public void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

}
