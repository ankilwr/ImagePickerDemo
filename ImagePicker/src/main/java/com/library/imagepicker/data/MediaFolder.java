package com.library.imagepicker.data;

import android.net.Uri;

import java.util.ArrayList;

/**
 * 图片文件夹实体类

 * Date: 2018/8/23
 * Time: 上午12:56
 */
public class MediaFolder {

    private int folderId;
    private String folderName;
    private String folderCover;
    private Uri folderCoverUri;
    private boolean isCheck;
    private ArrayList<MediaFile> mediaFileList;

    public MediaFolder(int folderId, String folderName, ArrayList<MediaFile> mediaFileList) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.mediaFileList = mediaFileList;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Uri getFolderCoverUri() {
        if(folderCoverUri == null){
            if(mediaFileList != null && !mediaFileList.isEmpty()){
                folderCoverUri = mediaFileList.get(0).getUri();
            }
        }
        return folderCoverUri;
    }

    public void setFolderCoverUri(Uri folderCoverUri) {
        this.folderCoverUri = folderCoverUri;
    }

    public String getFolderCover() {
        if(folderCover == null){
            if(mediaFileList != null && !mediaFileList.isEmpty()){
                folderCover = mediaFileList.get(0).getPath();
            }
        }
        return folderCover;
    }

    public MediaFile getFolderCoverMedia() {
        return mediaFileList.get(0);
    }

    public void setFolderCover(String folderCover) {
        this.folderCover = folderCover;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public ArrayList<MediaFile> getMediaFileList() {
        return mediaFileList;
    }

    public void setMediaFileList(ArrayList<MediaFile> mediaFileList) {
        this.mediaFileList = mediaFileList;
    }

    public MediaFolder init(){
        getFolderCover();
        getFolderCoverUri();
        return this;
    }

}
