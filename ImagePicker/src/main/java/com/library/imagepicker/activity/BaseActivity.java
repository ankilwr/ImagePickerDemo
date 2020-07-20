package com.library.imagepicker.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * BaseActivity基类

 * Date: 2018/10/9
 * Time: 下午11:34
 */
public abstract class BaseActivity extends AppCompatActivity {

    private View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mView == null) {
            mView = View.inflate(this, bindLayout(), null);
        }
        setContentView(mView);

        initConfig();
        initView();
        initListener();
        getData();
    }


    protected abstract int bindLayout();

    protected void initConfig() {
    }

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void getData();


}
