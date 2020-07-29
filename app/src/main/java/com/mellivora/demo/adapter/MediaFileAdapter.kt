package com.mellivora.demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mellivora.demo.R
import com.imagepicket.ImagePickerLoader
import com.mellivora.imagepicker.data.MediaFile
import kotlinx.android.synthetic.main.item_image.view.*


class MediaFileAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var datas: MutableList<MediaFile>? = null
        private set

    private val imageLoader = ImagePickerLoader()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object: RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bean = datas!![position]
        imageLoader.loadImage(holder.itemView.ivImage, bean)
    }

    override fun getItemCount(): Int {
        return datas?.size?:0
    }

    fun resetNotify(datas: MutableList<MediaFile>?){
        this.datas = datas
        notifyDataSetChanged()
    }

}
