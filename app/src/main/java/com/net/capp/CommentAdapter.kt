package com.net.capp

import SharedPref
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommentAdapter(private val dataList: MutableList<commentItem>, private val context: Context) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val itemClickListener: CommentAdapter.OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data,context)
    }

    override fun getItemCount(): Int = dataList.size

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.usernameComment)
        private val comment: TextView = itemView.findViewById(R.id.commentComment)
        private val like: ImageView = itemView.findViewById(R.id.likeComment)
        private val delete:TextView = itemView.findViewById(R.id.deleteComment)

        init {
            like.setOnClickListener {
                SharedPref.initialize(context)
                val userId = SharedPref.getUserId().toString()
                val commentid = dataList.get(position).id

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://c-app.in/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val apiService = retrofit.create(ApiService::class.java)
                val request = postlikeComment(commentid,userId)
                val response = apiService.likeComment(request)

                response.enqueue(object: Callback<isSuccess> {
                    override fun onResponse(call: Call<isSuccess>, response: Response<isSuccess>) {
                        val body = response.body()?.success
                        if (body == true){
                            like.setImageResource(R.drawable.liked)
                        }
                    }
                    override fun onFailure(call: Call<isSuccess>, t: Throwable) {
                        like.setImageResource(R.drawable.heart)
                    }
                })
            }
            delete.setOnClickListener {
                val progressDialog = Dialog(context)
                progressDialog.setCancelable(false)
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                progressDialog.setContentView(R.layout.progress_dialog)

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://c-app.in/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val apiService = retrofit.create(ApiService::class.java)
                val response = apiService.deleteComment(dataList.get(position).id)

                response.enqueue(object: Callback<isSuccess> {
                    override fun onResponse(call: Call<isSuccess>, response: Response<isSuccess>) {
                        val body = response.body()
                        dataList.removeAt(position)
                        notifyDataSetChanged()
                        progressDialog.dismiss()
                    }
                    override fun onFailure(call: Call<isSuccess>, t: Throwable) {
                    }
                })
            }
        }

        fun bind(
            data: commentItem,
            context: Context
        ) {
            SharedPref.initialize(context)
            val userId = SharedPref.getUserId().toString()

            userName.text = data.name
            comment.text = data.comment
            if (data.userid != userId){
                delete.visibility = View.GONE
            }
            else{
                delete.visibility = View.VISIBLE
            }
            if (data.like == "1"){
                like.setImageResource(R.drawable.liked)
            }
        }
    }
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
}