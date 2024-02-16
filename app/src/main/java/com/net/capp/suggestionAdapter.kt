package com.net.capp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class suggestionAdapter(private val dataList: List<newsItem>, private val context: Context) :
    RecyclerView.Adapter<suggestionAdapter.suggestionViewHolder>() {

    private val itemClickListener: suggestionAdapter.OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): suggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_suggestion, parent, false)
        return suggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: suggestionViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data,context)
    }

    override fun getItemCount(): Int = dataList.size

    inner class suggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.suggUsername)
        private val comment: TextView = itemView.findViewById(R.id.suggComment)
        private val image: ImageView = itemView.findViewById(R.id.suggImage)
        private val content: LinearLayout = itemView.findViewById(R.id.contentSugg)

        init {
            content.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(position)
                }
                val intent = Intent(context, NewsDetailsActivity::class.java)
                intent.putExtra("userId", dataList.get(position).user)
                intent.putExtra("newsId", dataList.get(position).id)
                context.startActivity(intent)
            }
        }

        fun bind(
            data: newsItem,
            context: Context
        ) {
            userName.text = data.user
            comment.text = data.comment
            Glide.with(context)
                .load("https://c-app.in/uploads/register/${data.image.get(0)}")
                .into(image)
        }
    }
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
}