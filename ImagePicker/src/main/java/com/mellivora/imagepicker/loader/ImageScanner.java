package com.mellivora.imagepicker.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.mellivora.imagepicker.data.MediaFile;
import com.mellivora.imagepicker.provider.ImagePickerProvider;

import java.io.File;

/**
 * 媒体库扫描类(图片)
 * <p>
 * Date: 2018/8/21
 * Time: 上午1:01
 */
public class ImageScanner extends AbsMediaScanner<MediaFile> {

    public ImageScanner(Context context) {
        super(context);
    }


    @Override
    protected Uri getScanUri() {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };
    }

    @Override
    protected String getSelection() {
        return MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?" + " or " + MediaStore.Images.Media.MIME_TYPE + "=?";
    }

    @Override
    protected String[] getSelectionArgs() {
        return new String[]{"image/jpeg", "image/png", "image/gif"};
    }

    @Override
    protected String getOrder() {
        return MediaStore.Images.Media.DATE_TAKEN + " desc";
    }

    /**
     * 构建媒体对象
     *
     * @param cursor
     * @return
     */
    @Override
    protected MediaFile parse(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
        Integer folderId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        long dateToken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = getScanUri().buildUpon().appendPath(id).build();
        } else {
            uri = FileProvider.getUriForFile(getContext(), ImagePickerProvider.getFileProviderName(getContext()), new File(path));
        }
        //Uri uri = getScanUri().buildUpon().appendPath(id).build();
        MediaFile mediaFile = new MediaFile();
        mediaFile.setUri(uri);
        mediaFile.setPath(path);
        mediaFile.setMime(mime);
        mediaFile.setFolderId(folderId);
        mediaFile.setFolderName(folderName);
        mediaFile.setDateToken(dateToken);

        return mediaFile;
    }


}
