package com.example.androidqtest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.base.library.utils.FileUtils
import com.base.library.utils.LogUtil
import com.example.androidqtest.adapter.MediaFileAdapter
import com.example.androidqtest.adapter.PictureFileAdapter
import com.glide.GlideApp
import com.glide.loadDefault
import com.imagepicket.ImagePickerLoader
import com.library.imagepicker.ImagePicker
import com.library.imagepicker.data.MediaFile
import com.soundcloud.android.crop.Crop
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_many.*
import kotlinx.coroutines.*
import top.zibin.luban.Luban
import java.io.File

class ManyActivity : AppCompatActivity() {

    private val PICKER_CODE: Int = 101
    private lateinit var mediaAdapter: MediaFileAdapter
    private lateinit var fileAdapter: PictureFileAdapter

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_many)

        rvOriginal.layoutManager = GridLayoutManager(this, 3)
        mediaAdapter = MediaFileAdapter()
        rvOriginal.adapter = mediaAdapter

        rvThumb.layoutManager = GridLayoutManager(this, 3)
        fileAdapter = PictureFileAdapter()
        rvThumb.adapter = fileAdapter

        btnChoose.setOnClickListener {
            ImagePicker.getInstance()
                .setTitle("图库")
                .showCamera(true)
                .setPickerType(ImagePicker.IMAGE)
                .setImageSingleEnable(false)
                .setMaxCount(9)
                .setImagePaths(mediaAdapter.datas as ArrayList<MediaFile>?)
                .setImageLoader(ImagePickerLoader())
                .start(this, PICKER_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PICKER_CODE -> {
                    val pathList: ArrayList<MediaFile> = data.getParcelableArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES)!!
                    thumb(pathList)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelJob.cancel()
    }

    fun thumb(mediaList: MutableList<MediaFile>){
        uiScope.launch {
            LogUtil.i("测试测试", "1(id:${Thread.currentThread().id}) -> time:${System.currentTimeMillis()}")
            val job1 = uiScope.async {
                LogUtil.i("测试测试", "2Start(id:${Thread.currentThread().id}) -> time:${System.currentTimeMillis()}")
                val uris = mutableListOf<Uri>()
                mediaList.forEach { uris.add(it.uri) }
                Thread.sleep(10000)
                val aa = Luban.with(this@ManyActivity).load(uris).ignoreBy(200).setTargetDir(FileUtils.getSdCache(FileUtils.Temp).path).get()
                LogUtil.i("测试测试", "2End(id:${Thread.currentThread().id}) -> time:${System.currentTimeMillis()}")
                aa
            }
            val files: MutableList<File>? = job1.await()
            files?.forEach {
                LogUtil.i("测试测试", "path:${it.path}")
                LogUtil.i("测试测试", "uri:${it.canonicalPath}")
            }
            LogUtil.i("测试测试", "3(id:${Thread.currentThread().id}) -> time:${System.currentTimeMillis()}")
            fileAdapter.resetNotify(files)
        }
        mediaAdapter.resetNotify(mediaList)
    }


}
