package com.example.androidqtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidqtest.R
import com.glide.GlideApp
import com.imagepicket.ImagePickerLoader
import com.library.imagepicker.data.MediaFile
import kotlinx.android.synthetic.main.item_image.view.*
import java.io.File


class PictureFileAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var datas: MutableList<File>? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object: RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bean = datas!![position]
        GlideApp.with(holder.itemView.ivImage).load(bean).into(holder.itemView.ivImage)
    }

    override fun getItemCount(): Int {
        return datas?.size?:0
    }

    fun resetNotify(datas: MutableList<File>?){
        this.datas = datas
        notifyDataSetChanged()
    }

}
