package com.mellivora.imagepicker.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.mellivora.imagepicker.ImagePicker;
import com.mellivora.imagepicker.R;
import com.mellivora.imagepicker.adapter.ImagePreViewAdapter;
import com.mellivora.imagepicker.data.MediaFile;
import com.mellivora.imagepicker.manager.ConfigManager;
import com.mellivora.imagepicker.manager.SelectionManager;
import com.mellivora.imagepicker.utils.DataUtil;
import com.mellivora.imagepicker.utils.MediaFileUtil;
import com.mellivora.imagepicker.listener.VideoLoader;
import com.mellivora.imagepicker.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 大图预览界面
 * <p>
 * Date: 2018/10/3
 * Time: 下午11:32
 */
public class ImagePreActivity extends BaseActivity {

    public static final String IMAGE_POSITION = "imagePosition";
    private List<MediaFile> mMediaFileList;
    private int mPosition = 0;

    private TextView mTvTitle;
    private TextView mTvCommit;
    private ImageView mIvPlay;
    private HackyViewPager mViewPager;
    private LinearLayout mLlPreSelect;
    private ImageView mIvPreCheck;
    private View checkOriginal;
    private ImagePreViewAdapter mImagePreViewAdapter;


    @Override
    protected int bindLayout() {
        return R.layout.activity_pre_image;
    }

    @Override
    protected void initView() {
        mTvTitle = findViewById(R.id.tv_actionBar_title);
        checkOriginal = findViewById(R.id.checkOriginal);
        mTvCommit = findViewById(R.id.tv_actionBar_commit);
        mIvPlay = findViewById(R.id.iv_main_play);
        mViewPager = findViewById(R.id.vp_main_preImage);
        mLlPreSelect = findViewById(R.id.ll_pre_select);
        mIvPreCheck = findViewById(R.id.iv_item_check);

        checkOriginal.setSelected(getIntent().getBooleanExtra(ImagePicker.EXTRA_ORIGINAL, false));
        if (ConfigManager.getInstance().isOriginalEnable()) {
            checkOriginal.setVisibility(View.VISIBLE);
        } else {
            checkOriginal.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListener() {

        findViewById(R.id.iv_actionBar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        checkOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvTitle.setText(String.format("%s/%s", position + 1, mMediaFileList.size()));
                setIvPlayShow(mMediaFileList.get(position));
                updateSelectButton(mMediaFileList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mLlPreSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaFile mediaFile = mMediaFileList.get(mViewPager.getCurrentItem());
                //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
                if (ConfigManager.getInstance().isSingleType()) {
                    ArrayList<MediaFile> selectPathList = SelectionManager.getInstance().getSelectPaths();
                    if (!selectPathList.isEmpty()) {
                        //判断选中集合中第一项是否为视频
                        if (!SelectionManager.isCanAddSelectionPaths(mMediaFileList.get(mViewPager.getCurrentItem()), selectPathList.get(0))) {
                            //类型不同
                            ConfigManager.getInstance().getImageLoader().showToast(ImagePreActivity.this, getString(R.string.single_type_choose));
                            return;
                        }
                        //要添加的图片/视频没选中的时候，才需要去清除其他选中的图片或视屏
                        if(!SelectionManager.getInstance().isImageSelect(mediaFile)){
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
                    updateSelectButton(mediaFile);
                    updateCommitButton();
                } else {
                    String message = String.format(getString(R.string.select_image_max), SelectionManager.getInstance().getMaxCount());
                    ConfigManager.getInstance().getImageLoader().showToast(ImagePreActivity.this, message);
                }
            }
        });

        mTvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_ORIGINAL, checkOriginal.isSelected());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaFile videoFile = mMediaFileList.get(mViewPager.getCurrentItem());
                VideoLoader videoLoader = ConfigManager.getInstance().getVideoLoader();
                if(videoLoader != null && videoLoader.onVideoClick(ImagePreActivity.this, videoFile, ConfigManager.getInstance())){
                    return;
                }
                VideoPlayerActivity.start(ImagePreActivity.this, videoFile);

                //实现播放视频的跳转逻辑(调用原生视频播放器)
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Uri uri = FileProvider.getUriForFile(ImagePreActivity.this, ImagePickerProvider.getFileProviderName(ImagePreActivity.this), new File(videoFile.getPath()));
//                intent.setDataAndType(uri, "video/*");
//                //给所有符合跳转条件的应用授权
//                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                for (ResolveInfo resolveInfo : resInfoList) {
//                    String packageName = resolveInfo.activityInfo.packageName;
//                    grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                }
//                startActivity(intent);
            }
        });

    }

    @Override
    protected void getData() {
        mMediaFileList = DataUtil.getInstance().getMediaData();
        mPosition = getIntent().getIntExtra(IMAGE_POSITION, 0);
        mTvTitle.setText(String.format("%s/%s", mPosition + 1, mMediaFileList.size()));
        mImagePreViewAdapter = new ImagePreViewAdapter(this, mMediaFileList);
        mViewPager.setAdapter(mImagePreViewAdapter);
        mViewPager.setCurrentItem(mPosition);
        //更新当前页面状态
        setIvPlayShow(mMediaFileList.get(mPosition));
        updateSelectButton(mMediaFileList.get(mPosition));
        updateCommitButton();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.EXTRA_ORIGINAL, checkOriginal.isSelected());
        setResult(Activity.RESULT_CANCELED, intent);
        super.onBackPressed();
    }

    /**
     * 更新确认按钮状态
     */
    private void updateCommitButton() {
        int maxCount = SelectionManager.getInstance().getMaxCount();
        //改变确定按钮UI
        int selectCount = SelectionManager.getInstance().getSelectPaths().size();
        if (selectCount == 0) {
            mTvCommit.setEnabled(false);
            mTvCommit.setText(getString(R.string.confirm));
            return;
        }
        if (selectCount < maxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.confirm_msg), selectCount, maxCount));
            if(ConfigManager.getInstance().isSingleType()){
                MediaFile firstMedia = SelectionManager.getInstance().getSelectPaths().get(0);
                if (MediaFileUtil.isVideoFileType(firstMedia) && ConfigManager.getInstance().isVideoSingle()) {
                    mTvCommit.setText(String.format(getString(R.string.confirm_msg), 1, 1));
                } else if (MediaFileUtil.isImageFileType(firstMedia) && ConfigManager.getInstance().isImageSingle()) {
                    mTvCommit.setText(String.format(getString(R.string.confirm_msg), 1, 1));
                }
            }
            return;
        }
        if (selectCount == maxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.confirm_msg), selectCount, maxCount));
            return;
        }
    }

    /**
     * 更新选择按钮状态
     */
    private void updateSelectButton(MediaFile mediaFile) {
        boolean isSelect = SelectionManager.getInstance().isImageSelect(mediaFile);
        if (isSelect) {
            mIvPreCheck.setImageDrawable(getResources().getDrawable(R.mipmap.icon_image_checked));
        } else {
            mIvPreCheck.setImageDrawable(getResources().getDrawable(R.mipmap.icon_image_check));
        }
    }

    /**
     * 设置是否显示视频播放按钮
     *
     * @param mediaFile
     */
    private void setIvPlayShow(final MediaFile mediaFile) {
        if (mediaFile.getDuration() > 0) {
            mIvPlay.setVisibility(View.VISIBLE);
        } else {
            mIvPlay.setVisibility(View.GONE);
        }
    }

}
