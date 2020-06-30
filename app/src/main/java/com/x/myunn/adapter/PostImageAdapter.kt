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
import com.x.myunn.model.PostImage
import com.x.myunn.utils.glideLoad


class PostImageAdapter(var mPostImages: MutableList<PostImage>?, var c: Context, var page: Int) :
    RecyclerView.Adapter<PostImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(c).inflate(R.layout.item_layout_post_images, parent, false)

        Log.d("TAG-PIA", "      onCreateViewHolder")

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        if (mPostImages != null) {
            Log.d("TAG-PIA", "      getItemCount(): ${mPostImages!!.size}")
            return mPostImages!!.size
        }else
            return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postImage = mPostImages!![position]

        holder.cancel.visibility = View.GONE

        glideLoad(c, postImage.imageUrl, holder.imageView, true)

        Log.d("TAG-PIA", "    onBindViewHolder  ")

        holder.itemView.setOnClickListener {

            Log.d("TAG-PIA", "    OnCLicked....  ")

        }

    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var imageView = v.findViewById<ImageView>(R.id.post_image)
        var cancel = v.findViewById<ImageButton>(R.id.cancel_image)
    }
}