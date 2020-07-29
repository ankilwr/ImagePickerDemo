package com.mellivora.imagepicker.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mellivora.imagepicker.BuildConfig;
import com.mellivora.imagepicker.ImagePicker;
import com.mellivora.imagepicker.R;
import com.mellivora.imagepicker.adapter.ImageFoldersAdapter;
import com.mellivora.imagepicker.adapter.ImagePickerAdapter;
import com.mellivora.imagepicker.data.MediaFile;
import com.mellivora.imagepicker.data.MediaFolder;
import com.mellivora.imagepicker.executors.CommonExecutor;
import com.mellivora.imagepicker.listener.MediaLoadCallback;
import com.mellivora.imagepicker.listener.OnCameraListener;
import com.mellivora.imagepicker.manager.ConfigManager;
import com.mellivora.imagepicker.manager.SelectionManager;
import com.mellivora.imagepicker.provider.ImagePickerProvider;
import com.mellivora.imagepicker.task.ImageLoadTask;
import com.mellivora.imagepicker.task.MediaLoadTask;
import com.mellivora.imagepicker.task.VideoLoadTask;
import com.mellivora.imagepicker.utils.DataUtil;
import com.mellivora.imagepicker.utils.MediaFileUtil;
import com.mellivora.imagepicker.listener.OnTakePictureListener;
import com.mellivora.imagepicker.utils.PermissionUtil;
import com.mellivora.imagepicker.view.ImageFolderPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 多图选择器主页面
 * Date: 2018/8/23
 * Time: 上午1:10
 */
public class ImagePickerActivity extends BaseActivity implements ImagePickerAdapter.OnItemClickListener, ImageFoldersAdapter.OnImageFolderChangeListener, OnCameraListener {

    /**
     * 启动参数
     */
    private String mTitle;
    private boolean isShowCamera;
    private String pickerType;
    private boolean isSingleType;
    private int mMaxCount;
    private List<MediaFile> mImagePaths;

    /**
     * 界面UI
     */
    private TextView mTvTitle;
    private TextView mTvCommit;
    private RecyclerView mRecyclerView;
    private TextView mTvImageFolders;
    private ImageFolderPopupWindow mImageFolderPopupWindow;
    private ProgressDialog mProgressDialog;
    private RelativeLayout mRlBottom;
    private View checkOriginal;

    private ImagePickerAdapter mImagePickerAdapter;

    //图片数据源
    private List<MediaFile> mMediaFileList;
    //文件夹数据源
    private List<MediaFolder> mMediaFolderList;

    //表示屏幕亮暗
    private static final int LIGHT_OFF = 0;
    private static final int LIGHT_ON = 1;


    /**
     * 大图预览页相关
     */
    private static final int REQUEST_SELECT_IMAGES_CODE = 0x01;//用于在大图预览页中点击提交按钮标识


    /**
     * 拍照相关
     */
    private MediaFile cameraMediaFile;
    private static final int REQUEST_CODE_CAPTURE = 0x02;//拍照
    private static final int REQUEST_CODE_VIDEO = 0x12;  //录像

    /**
     * 权限相关
     */
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 0x03;


    @Override
    protected int bindLayout() {
        return R.layout.activity_imagepicker;
    }


    /**
     * 初始化配置
     */
    @Override
    protected void initConfig() {
        mTitle = ConfigManager.getInstance().getTitle();
        isShowCamera = ConfigManager.getInstance().isShowCamera();
        pickerType = ConfigManager.getInstance().getPickerType();
        isSingleType = ConfigManager.getInstance().isSingleType();
        mMaxCount = ConfigManager.getInstance().getMaxCount();
        SelectionManager.getInstance().setMaxCount(mMaxCount);

        //载入历史选择记录
        mImagePaths = ConfigManager.getInstance().getImagePaths();
        if (mImagePaths != null && !mImagePaths.isEmpty()) {
            SelectionManager.getInstance().addImagePathsToSelectList(mImagePaths);
        }
    }


    /**
     * 初始化布局控件
     */
    @Override
    protected void initView() {

        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.scanner_image));

        //顶部栏相关
        mTvTitle = findViewById(R.id.tv_actionBar_title);
        if (TextUtils.isEmpty(mTitle)) {
            mTvTitle.setText(getString(R.string.image_picker));
        } else {
            mTvTitle.setText(mTitle);
        }
        mTvCommit = findViewById(R.id.tv_actionBar_commit);

        //底部栏相关
        checkOriginal = findViewById(R.id.checkOriginal);
        mRlBottom = findViewById(R.id.rl_main_bottom);
        mTvImageFolders = findViewById(R.id.tv_main_imageFolders);

        //列表相关
        mRecyclerView = findViewById(R.id.rv_main_images);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        //注释说当知道Adapter内Item的改变不会影响RecyclerView宽高的时候，可以设置为true让RecyclerView避免重新计算大小。
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(60);

        mMediaFileList = new ArrayList<>();
        mImagePickerAdapter = new ImagePickerAdapter(this, mMediaFileList);
        mImagePickerAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mImagePickerAdapter);

        if (ConfigManager.getInstance().isOriginalEnable()) {
            checkOriginal.setVisibility(View.VISIBLE);
        } else {
            checkOriginal.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化控件监听事件
     */
    @Override
    protected void initListener() {
        findViewById(R.id.iv_actionBar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mTvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitSelection();
            }
        });

        checkOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        mTvImageFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageFolderPopupWindow != null) {
                    setLightMode(LIGHT_OFF);
                    mImageFolderPopupWindow.showAsDropDown(mRlBottom, 0, 0);
                }
            }
        });
    }

    /**
     * 获取数据源
     */
    @Override
    protected void getData() {
        //进行权限的判断
        boolean hasPermission = PermissionUtil.checkPermission(this);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA_CODE);
        } else {
            startScannerTask();
        }
    }

    /**
     * 权限申请回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            if (grantResults.length >= 1) {
                int cameraResult = grantResults[0];//相机权限
                int sdResult = grantResults[1];//sd卡权限
                boolean cameraGranted = cameraResult == PackageManager.PERMISSION_GRANTED;//拍照权限
                boolean sdGranted = sdResult == PackageManager.PERMISSION_GRANTED;//拍照权限
                if (cameraGranted && sdGranted) {
                    //具有拍照权限，sd卡权限，开始扫描任务
                    startScannerTask();
                } else {
                    //没有权限
                    ConfigManager.getInstance().getImageLoader().showToast(ImagePickerActivity.this, getString(R.string.permission_tip));
                    finish();
                }
            }
        }
    }


    /**
     * 开启扫描任务
     */
    private void startScannerTask() {
        Runnable mediaLoadTask = null;
        if (ImagePicker.VIDEO.equals(pickerType)) {
            //只加载视频
            mediaLoadTask = new VideoLoadTask(this, new MediaLoader());
        } else if (ImagePicker.IMAGE.equals(pickerType)) {
            //只加载图片
            mediaLoadTask = new ImageLoadTask(this, new MediaLoader());
        } else {
            //不符合以上场景，采用照片、视频全部加载
            mediaLoadTask = new MediaLoadTask(this, new MediaLoader());
        }
        CommonExecutor.getInstance().execute(mediaLoadTask);
    }


    /**
     * 处理媒体数据加载成功后的UI渲染
     */
    class MediaLoader implements MediaLoadCallback {
        @Override
        public void loadMediaSuccess(final List<MediaFolder> mediaFolderList) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mediaFolderList.isEmpty()) {
                        //默认加载全部照片
                        mMediaFileList.clear();
                        mMediaFileList.addAll(mediaFolderList.get(0).getMediaFileList());
                        mImagePickerAdapter.notifyDataSetChanged();

                        //图片文件夹数据
                        mMediaFolderList = new ArrayList<>(mediaFolderList);
                        mImageFolderPopupWindow = new ImageFolderPopupWindow(ImagePickerActivity.this, mMediaFolderList);
                        mImageFolderPopupWindow.setAnimationStyle(R.style.imageFolderAnimator);
                        mImageFolderPopupWindow.getAdapter().setOnImageFolderChangeListener(ImagePickerActivity.this);
                        mImageFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                setLightMode(LIGHT_ON);
                            }
                        });
                        updateCommitButton();
                    }
                    mProgressDialog.cancel();
                }
            });
        }
    }


    /**
     * 设置屏幕的亮度模式
     *
     * @param lightMode
     */
    private void setLightMode(int lightMode) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        switch (lightMode) {
            case LIGHT_OFF:
                layoutParams.alpha = 0.7f;
                break;
            case LIGHT_ON:
                layoutParams.alpha = 1.0f;
                break;
        }
        getWindow().setAttributes(layoutParams);
    }

    /**
     * 点击图片
     *
     * @param view
     * @param position
     */
    @Override
    public void onMediaClick(View view, int position) {
        if (isShowCamera) {
            if (position == 0) {
                if (!SelectionManager.getInstance().isCanChoose()) {
                    ConfigManager.getInstance().getImageLoader().showToast(ImagePickerActivity.this, String.format(getString(R.string.select_image_max), mMaxCount));
                    return;
                }
                showCamera();
                return;
            }
        }

        if (mMediaFileList != null) {
            DataUtil.getInstance().setMediaData(mMediaFileList);
            Intent intent = new Intent(this, ImagePreActivity.class);
            if (isShowCamera) {
                intent.putExtra(ImagePreActivity.IMAGE_POSITION, position - 1);
            } else {
                intent.putExtra(ImagePreActivity.IMAGE_POSITION, position);
            }
            intent.putExtra(ImagePicker.EXTRA_ORIGINAL, checkOriginal.isSelected());
            startActivityForResult(intent, REQUEST_SELECT_IMAGES_CODE);
        }
    }

    /**
     * 选中/取消选中图片
     *
     * @param view
     * @param position
     */
    @Override
    public void onMediaCheck(View view, int position) {
        if (isShowCamera) {
            if (position == 0) {
                if (!SelectionManager.getInstance().isCanChoose()) {
                    ConfigManager.getInstance().getImageLoader().showToast(ImagePickerActivity.this, String.format(getString(R.string.select_image_max), mMaxCount));
                    return;
                }
                showCamera();
                return;
            }
        }

        //执行选中/取消操作
        MediaFile mediaFile = mImagePickerAdapter.getMediaFile(position);
        if (mediaFile != null) {
            if (isSingleType) {
                //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
                ArrayList<MediaFile> selectPathList = SelectionManager.getInstance().getSelectPaths();
                if (!selectPathList.isEmpty()) {
                    //判断选中集合中第一项是否为视频
                    if (!SelectionManager.isCanAddSelectionPaths(mediaFile, selectPathList.get(0))) {
                        //类型不同
                        ConfigManager.getInstance().getImageLoader().showToast(ImagePickerActivity.this, getString(R.string.single_type_choose));
                        return;
                    }
                    //要添加的图片/视频没选中的时候，才需要去清除其他选中的图片或视屏
                    if (!SelectionManager.getInstance().isImageSelect(mediaFile)) {
                        MediaFile firstMedia = selectPathList.get(0);
                        if (MediaFileUtil.isVideoFileType(firstMedia) && ConfigManager.getInstance().isVideoSingle()) {
                            SelectionManager.getInstance().removeAll();
                        } else if (MediaFileUtil.isImageFileType(firstMedia) && ConfigManager.getInstance().isImageSingle()) {
                            SelectionManager.getInstance().removeAll();
                        }
                    }
                }
            }
            boolean addSuccess = SelectionManager.getInstance().addImageToSelectList(mediaFile);
            if (addSuccess) {
//                mImagePickerAdapter.notifyItemChanged(position);
                mImagePickerAdapter.notifyDataSetChanged();
            } else {
                ConfigManager.getInstance().getImageLoader().showToast(ImagePickerActivity.this, String.format(getString(R.string.select_image_max), mMaxCount));
            }
        }
        updateCommitButton();
    }

    /**
     * 更新确认按钮状态
     */
    private void updateCommitButton() {
        //改变确定按钮UI
        int selectCount = SelectionManager.getInstance().getSelectPaths().size();
        if (selectCount == 0) {
            mTvCommit.setEnabled(false);
            mTvCommit.setText(getString(R.string.confirm));
            return;
        }
        if (selectCount < mMaxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.confirm_msg), selectCount, mMaxCount));
            if (ConfigManager.getInstance().isSingleType()) {
                MediaFile firstMedia = SelectionManager.getInstance().getSelectPaths().get(0);
                if (MediaFileUtil.isVideoFileType(firstMedia) && ConfigManager.getInstance().isVideoSingle()) {
                    mTvCommit.setText(String.format(getString(R.string.confirm_msg), 1, 1));
                } else if (MediaFileUtil.isImageFileType(firstMedia) && ConfigManager.getInstance().isImageSingle()) {
                    mTvCommit.setText(String.format(getString(R.string.confirm_msg), 1, 1));
                }
            }
            return;
        }
        if (selectCount == mMaxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.confirm_msg), selectCount, mMaxCount));
            return;
        }
    }

    /**
     * 跳转相机拍照
     */
    private void showCamera() {
        OnTakePictureListener takeListener = ConfigManager.getInstance().getOnTakePictureListener();
        //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
        ArrayList<MediaFile> selectPathList = SelectionManager.getInstance().getSelectPaths();
        if (isSingleType && !selectPathList.isEmpty()) {
            //如果是单类型选择并且已有选择文件
            if (takeListener != null) {
                if (MediaFileUtil.isVideoFileType(selectPathList.get(0))) {
                    takeListener.onTakePicture(this, ImagePicker.VIDEO, this, REQUEST_CODE_CAPTURE, REQUEST_CODE_VIDEO);
                } else {
                    takeListener.onTakePicture(this, ImagePicker.IMAGE, this, REQUEST_CODE_CAPTURE, REQUEST_CODE_VIDEO);
                }
            } else {
                if (MediaFileUtil.isVideoFileType(selectPathList.get(0))) {
                    openVideoRecord(this, REQUEST_CODE_VIDEO);
                } else {
                    openCamera(this, REQUEST_CODE_CAPTURE);
                }
            }
            return;
        }
        if (takeListener != null) {
            takeListener.onTakePicture(this, ConfigManager.getInstance().getPickerType(), this, REQUEST_CODE_CAPTURE, REQUEST_CODE_VIDEO);
            return;
        }
        if (ImagePicker.VIDEO.equals(pickerType)) {
            openVideoRecord(this, REQUEST_CODE_VIDEO);
        } else {
            openCamera(this, REQUEST_CODE_CAPTURE);
        }
    }

    @Override
    public void openCamera(Activity context, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Uri uri;
//        if (Build.VERSION.SDK_INT >= 24) {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, String.format("IMG_%s.jpg", System.currentTimeMillis()));
//            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
//            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "image/jpg");
//            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//            //uri = FileProvider.getUriForFile(this, ImagePickerProvider.getFileProviderName(this), new File(mFilePath));
//        } else {
//            uri = Uri.fromFile(new File(mFilePath));
//        }
        cameraMediaFile = new MediaFile();
        String mime = "image/jpg";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, String.format("IMG_%s.jpg", System.currentTimeMillis()));
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            //RELATIVE_PATH 在androidQ以上才生效
            values.put(MediaStore.Images.Media.RELATIVE_PATH, String.format("Pictures/%s/", context.getString(R.string.picker_name)));
            values.put(MediaStore.Images.Media.MIME_TYPE, mime);
            values.put(MediaStore.Images.Media.TITLE, "Image");
            values.put(MediaStore.Images.Media.DESCRIPTION, "this is an image");
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            cameraMediaFile.setPath(uri.toString());
            cameraMediaFile.setUri(uri);
        }else{
            //拍照存放路径
            File fileDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            String mFilePath = String.format("%s/IMG_%s.jpg", fileDir.getPath(), System.currentTimeMillis());
            cameraMediaFile.setPath(mFilePath);
            cameraMediaFile.setUri(FileProvider.getUriForFile(this, ImagePickerProvider.getFileProviderName(this), new File(mFilePath)));
        }
        Uri uri = cameraMediaFile.getUri();
        Log.i("拍照", String.format("打开相机(saveUri): %s", uri));
        cameraMediaFile.setRemote(false);
        cameraMediaFile.setMime(mime);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE);
    }

    @Override
    public void openVideoRecord(Activity context, int requestCode) {

    }

    @Override
    public void onVideoRecordSuccess(String path) {
        if (BuildConfig.DEBUG) Log.i("onVideoRecordSuccess", "onVideoRecordSuccess: " + path);
        if (ConfigManager.getInstance().isVideoSingle()) {
            SelectionManager.getInstance().removeAll();
        }
        SelectionManager.getInstance().addImageToSelectList(MediaFileUtil.createMediaFile(path, false));
        MediaScannerConnection.scanFile(this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                startScannerTask();
                if (BuildConfig.DEBUG) Log.i("onVideoRecordSuccess", "scannerSuccess()");
            }
        });
    }


    /**
     * 当图片文件夹切换时，刷新图片列表数据源
     *
     * @param view
     * @param position
     */
    @Override
    public void onImageFolderChange(View view, int position) {
        MediaFolder mediaFolder = mMediaFolderList.get(position);
        //更新当前文件夹名
        String folderName = mediaFolder.getFolderName();
        if (!TextUtils.isEmpty(folderName)) {
            mTvImageFolders.setText(folderName);
        }
        //更新图片列表数据源
        mMediaFileList.clear();
        mMediaFileList.addAll(mediaFolder.getMediaFileList());
        mImagePickerAdapter.notifyDataSetChanged();

        mImageFolderPopupWindow.dismiss();
    }

    /**
     * 拍照回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
            if (data != null) {
                checkOriginal.setSelected(data.getBooleanExtra(ImagePicker.EXTRA_ORIGINAL, false));
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAPTURE) {
                if (ConfigManager.getInstance().isImageSingle()) {
                    //单选图片的时候
//                    ArrayList<MediaFile> list = new ArrayList<>();
//                    list.add(cameraMediaFile);
//                    Intent intent = new Intent();
//                    intent.putParcelableArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, list);
//                    intent.putExtra(ImagePicker.EXTRA_ORIGINAL, checkOriginal.isSelected());
//                    setResult(RESULT_OK, intent);
//                    finish();
//                    return;
                    SelectionManager.getInstance().removeAll();
                }
                //多选图片的时候
                SelectionManager.getInstance().addImageToSelectList(cameraMediaFile);
                startScannerTask();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startScannerTask();
                } else {
                    MediaScannerConnection.scanFile(this, new String[]{cameraMediaFile.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            startScannerTask();
                        }
                    });
                }
            } else if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
                commitSelection();
            } else if (requestCode == REQUEST_CODE_VIDEO) {
                OnTakePictureListener takeListener = ConfigManager.getInstance().getOnTakePictureListener();
                if (takeListener != null) {
                    takeListener.onVideoRecordSuccess(data, this);
                } else {
                    //onVideoRecordSuccess();
                }
            }
        }
    }

    /**
     * 选择图片完毕，返回
     */
    private void commitSelection() {
        ArrayList<MediaFile> list = new ArrayList<>(SelectionManager.getInstance().getSelectPaths());
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.EXTRA_SELECT_IMAGES, list);
        setResult(RESULT_OK, intent);
        SelectionManager.getInstance().removeAll();//清空选中记录
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mImagePickerAdapter.notifyDataSetChanged();
        updateCommitButton();
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        try {
            SelectionManager.getInstance().removeAll();
            ConfigManager.getInstance().getImageLoader().clearMemoryCache(this);
            ConfigManager.getInstance().setOnTakePictureListener(null);
            ConfigManager.getInstance().setImageLoader(null);
            ConfigManager.getInstance().setVideoLoader(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}
