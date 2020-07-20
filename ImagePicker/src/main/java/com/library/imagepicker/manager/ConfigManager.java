package com.library.imagepicker.manager;

import com.library.imagepicker.ImagePicker;
import com.library.imagepicker.ImagePicker.Picker;
import com.library.imagepicker.data.MediaFile;
import com.library.imagepicker.listener.ImageLoader;
import com.library.imagepicker.listener.OnTakePictureListener;
import com.library.imagepicker.listener.VideoLoader;

import java.util.ArrayList;

/**
 * 统一配置管理类
 * <p>
 * Date: 2019/1/23
 * Time: 10:32 AM
 */
public class ConfigManager {

    private String title;//标题
    private boolean originalEnable = false;
    private boolean showCamera;//是否显示拍照Item，默认不显示
    private String pickerType = ImagePicker.ALL;//picker类型
    private boolean imageSingleType;//是否支持多选（图片）
    private boolean videoSingleType;//是否支持多选（视频）
    private boolean singleType;//是否只支持选单类型（视频/视频只能选一种）
    private int maxCount = 1;//最大选择数量，默认为1
    private ArrayList<MediaFile> imagePaths;//上一次选择的图片地址集合

    private ImageLoader imageLoader;
    private VideoLoader videoLoader;
    private OnTakePictureListener onTakePictureListener;

    private static volatile ConfigManager mConfigManager;

    private ConfigManager() {
    }

    public static ConfigManager getInstance() {
        if (mConfigManager == null) {
            synchronized (SelectionManager.class) {
                if (mConfigManager == null) {
                    mConfigManager = new ConfigManager();
                }
            }
        }
        return mConfigManager;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isOriginalEnable() {
        return originalEnable && ImagePicker.IMAGE.equals(pickerType);
    }

    public void setOriginalEnable(boolean enable) {
        this.originalEnable = enable;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public String getPickerType() {
        return pickerType;
    }

    public void setPickerType(@Picker String type) {
        this.pickerType = type;
    }


    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public ArrayList<MediaFile> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<MediaFile> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public boolean isImageSingle() {
        return imageSingleType;
    }

    public void setImageSingleEnable(boolean singleType) {
        this.imageSingleType = singleType;
    }

    public boolean isVideoSingle() {
        return videoSingleType;
    }

    public void setVideoSingleEnable(boolean singleType) {
        this.videoSingleType = singleType;
    }


    public boolean isSingleType(){
        return singleType;
    }

    public void setSingleType(boolean singleType) {
        this.singleType = singleType;
    }



    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }


    public void setVideoLoader(VideoLoader loader) {
        this.videoLoader = loader;
    }

    public VideoLoader getVideoLoader(){
        return videoLoader;
    }


    public void setOnTakePictureListener(OnTakePictureListener listener) {
        this.onTakePictureListener = listener;
    }

    public OnTakePictureListener getOnTakePictureListener(){
        return onTakePictureListener;
    }


}
