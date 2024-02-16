package com.net.capp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(val arraytwo: ArrayList<Uri>, postActivity: PostActivity) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.showimage, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageAdapter.ViewHolder, position: Int) {
        val ItemsViewModel = arraytwo[position]

        holder.postImage.setImageURI(ItemsViewModel)

        holder.close_image.setOnClickListener {

            arraytwo.removeAt(position)
            notifyDataSetChanged()
            notifyItemRemoved(position);

        }
    }

    public fun getImageArray() : ArrayList<Uri>{
        return arraytwo;
    }

    override fun getItemCount(): Int {
        return arraytwo.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val close_image: ImageView = itemView.findViewById(R.id.close_image)

    }
}