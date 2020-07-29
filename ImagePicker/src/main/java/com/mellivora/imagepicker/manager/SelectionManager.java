package com.mellivora.imagepicker.manager;

import com.mellivora.imagepicker.data.MediaFile;
import com.mellivora.imagepicker.utils.MediaFileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 媒体选择集合管理类
 * <p>
 * Date: 2018/8/23
 * Time: 上午1:19
 */
public class SelectionManager {

    private static volatile SelectionManager mSelectionManager;

    private ArrayList<MediaFile> mSelectImagePaths = new ArrayList<>();

    private int mMaxCount = 1;

    private SelectionManager() {
    }

    public static SelectionManager getInstance() {
        if (mSelectionManager == null) {
            synchronized (SelectionManager.class) {
                if (mSelectionManager == null) {
                    mSelectionManager = new SelectionManager();
                }
            }
        }
        return mSelectionManager;
    }

    /**
     * 设置最大选择数
     *
     * @param maxCount
     */
    public void setMaxCount(int maxCount) {
        this.mMaxCount = maxCount;
    }

    /**
     * 获取当前设置最大选择数
     *
     * @return
     */
    public int getMaxCount() {
        return this.mMaxCount;
    }

    /**
     * 获取当前所选图片集合path
     *
     * @return
     */
    public ArrayList<MediaFile> getSelectPaths() {
        return mSelectImagePaths;
    }

    /**
     * 添加/移除图片到选择集合
     *
     * @param mediaFile
     * @return
     */
    public boolean addImageToSelectList(MediaFile mediaFile) {
        for(MediaFile media: mSelectImagePaths){
            if(media.getUri().toString().equals(mediaFile.getUri().toString())){
                return mSelectImagePaths.remove(media);
            }
        }
        if (mSelectImagePaths.size() < mMaxCount) {
            return mSelectImagePaths.add(mediaFile);
        } else {
            return false;
        }
    }

    /**
     * 添加图片到选择集合
     * @param mediaFiles
     */
    public void addImagePathsToSelectList(List<MediaFile> mediaFiles) {
        if (mediaFiles != null) {
            for (int i = 0; i < mediaFiles.size(); i++) {
                MediaFile mediaFile = mediaFiles.get(i);
                if (isImageSelect(mediaFile) && mSelectImagePaths.size() < mMaxCount) {
                    mSelectImagePaths.add(mediaFile);
                }
            }
        }
    }


    /**
     * 判断当前图片是否被选择
     *
     * @param imagePath
     * @return
     */
    public boolean isImageSelect(MediaFile imagePath) {
        if (imagePath == null) return false;
        for (MediaFile chooseFile : mSelectImagePaths) {
            if(chooseFile.getUri() == null) continue;
            if (chooseFile.getUri().toString().equals(imagePath.getUri().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否还可以继续选择图片
     *
     * @return
     */
    public boolean isCanChoose() {
        if (getSelectPaths().size() < mMaxCount) {
            return true;
        }
        return false;
    }

    /**
     * 是否可以添加到选择集合（在singleType模式下，图片视频不能一起选）
     *
     * @param currentMedia
     * @param media
     * @return
     */
    public static boolean isCanAddSelectionPaths(MediaFile currentMedia, MediaFile media) {
        if ((MediaFileUtil.isVideoFileType(currentMedia) && !MediaFileUtil.isVideoFileType(media)) || (!MediaFileUtil.isVideoFileType(currentMedia) && MediaFileUtil.isVideoFileType(media))) {
            return false;
        }
//        if ((MediaFileUtil.isVideoFileType(currentPath) && !MediaFileUtil.isVideoFileType(filePath)) || (!MediaFileUtil.isVideoFileType(currentPath) && MediaFileUtil.isVideoFileType(filePath))) {
//            return false;
//        }
        return true;
    }

    /**
     * 清除已选图片
     */
    public void removeAll() {
        mSelectImagePaths.clear();
    }

}
