package com.mellivora.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.base.library.utils.FileUtils
import kotlinx.android.synthetic.main.activity_main.*

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
