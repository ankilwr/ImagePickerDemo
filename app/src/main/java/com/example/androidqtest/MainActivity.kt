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
import com.base.library.utils.FileUtils
import com.base.library.utils.LogUtil
import com.glide.GlideApp
import com.glide.loadDefault
import com.imagepicket.ImagePickerLoader
import com.library.imagepicker.ImagePicker
import com.library.imagepicker.data.MediaFile
import com.soundcloud.android.crop.Crop
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION_CAMERA_CODE: Int = 101
    private var cropUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FileUtils.init(this)

        btnCrop.setOnClickListener {
            val intent = Intent(this, CropActivity::class.java)
            startActivity(intent)
        }

        btnMany.setOnClickListener {
            val intent = Intent(this, ManyActivity::class.java)
            startActivity(intent)
        }
    }

}
