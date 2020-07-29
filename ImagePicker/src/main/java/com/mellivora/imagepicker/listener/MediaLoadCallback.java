package com.mellivora.imagepicker.listener;

import com.mellivora.imagepicker.data.MediaFolder;

import java.util.List;

/**
 * 图片扫描数据回调接口

 * Date: 2018/8/23
 * Time: 下午9:55
 */
public interface MediaLoadCallback {

    void loadMediaSuccess(List<MediaFolder> mediaFolderList);
}
