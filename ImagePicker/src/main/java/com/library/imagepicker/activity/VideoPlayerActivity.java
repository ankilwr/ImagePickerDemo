package com.library.imagepicker.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import com.library.imagepicker.R;
import com.library.imagepicker.data.MediaFile;

public class VideoPlayerActivity extends BaseActivity  {

    private VideoView videoView;
    private TextView tvError;
    private int currentPosition = 0;

    public static void start(Context context, MediaFile mediaFile){
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra("MediaFile", (Parcelable) mediaFile);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initView() {
        videoView = findViewById(R.id.videoView);
        tvError = findViewById(R.id.tvError);
        tvError.setVisibility(View.GONE);
    }

    @Override
    protected void initListener() {
        videoView.setOnCompletionListener(completionListener);
        videoView.setOnErrorListener(errorListener);
    }

    @Override
    protected void getData() {
        try {
            MediaFile videoFile = getIntent().getParcelableExtra("MediaFile");
            MediaController localMediaController = new MediaController(this);
            videoView.setMediaController(localMediaController);
            videoView.setVideoURI(videoFile.getUri());
            videoView.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        videoView.seekTo(currentPosition);
        super.onStart();
    }

    @Override
    protected void onPause() {
        videoView.pause();
        currentPosition = videoView.getCurrentPosition();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        super.onDestroy();
    }



    private OnCompletionListener completionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //finish();
//            videoView.seekTo(0);
//            videoView.stopPlayback();
        }
    };

    private OnErrorListener errorListener = new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            tvError.setVisibility(View.VISIBLE);
            return false;
        }
    };


}