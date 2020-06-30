package com.x.myunn.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.x.myunn.R
import com.x.myunn.model.Image
import com.x.myunn.utils.glideLoad
import java.io.File


class ImageAdapter(var mImages: ArrayList<Image>, var c: Context, var page: Int) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(c).inflate(R.layout.item_layout_post_images, parent, false)

        Log.d("TAG-A", "      onCreateViewHolder")

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        Log.d("TAG-A", "      getItemCount(): ${mImages.size}")

        return mImages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = mImages[position]

        if (page != 3){

            holder.cancel.visibility = View.GONE

            if (image.uri != null){
                glideLoad(c, image.uri!!, holder.imageView, false)
            }

        }else if (page == 3){

            holder.cancel.visibility = View.VISIBLE

            holder.imageView.setImageDrawable(image.bitmap)

            holder.cancel.setOnClickListener {

                removeAt(position, image.file!!)
            }

        }

        Log.d("TAG-A", "    onBindViewHolder  ")

        holder.itemView.setOnClickListener {

            Log.d("TAG-A", "    OnCLicked....  ")

        }

    }

    private fun removeAt(position: Int, path: File) {
        try {
            if (path.exists()){
                path.delete()
            }
        } catch (e: Exception) {

            Log.d("TAG-A", "No Delete !! : $path")
            e.printStackTrace()
        }
        mImages.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mImages.size)
        notifyDataSetChanged()
        Log.d(
            "TAG", "remove size = " + mImages.size
        )

    }

    fun getCurrentImageList(): ArrayList<String>? {

        val currentImages = arrayListOf<String>()

        if (mImages.isNotEmpty()) {

            for (image: Image in mImages)

                currentImages.add(image.uri!!)

            return currentImages
        }
        return null
    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var imageView = v.findViewById<ImageView>(R.id.post_image)
        var cancel = v.findViewById<ImageButton>(R.id.cancel_image)
    }
}