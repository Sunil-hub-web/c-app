package com.net.capp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ContentViewerAdapter(private val imageList: List<String>, private val context: Context) :
    RecyclerView.Adapter<ContentViewerAdapter.ContentViewHolder>() {

    private val itemClickListener: ContentViewerAdapter.OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_viewer, parent, false)
        return ContentViewHolder(view,context)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val image = imageList[position]
        holder.bind(image,context)
    }

    override fun getItemCount(): Int = imageList.size

    inner class ContentViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        private val imageViewer: ImageView = itemView.findViewById(R.id.imageViewer)

        init {
            imageViewer.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val image = "https://c-app.in/uploads/register/${imageList.get(position)}"
                val intent = Intent(context, ImageViewer::class.java)
                intent.putExtra("image",image)
                context.startActivity(intent)
            }
        }
        fun bind(image: String, context: Context) {
            Glide.with(context)
                .load("https://c-app.in/uploads/register/${image}")
                .into(imageViewer)
        }
    }
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
}