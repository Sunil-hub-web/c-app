package com.net.capp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FollowingAdapter(private val dataList: MutableList<Follower>, private val context: Context) :
    RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder>() {

    private val itemClickListener: FollowingAdapter.OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.follower_item_view, parent, false)
        return FollowingViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data,context)
    }

    override fun getItemCount(): Int = dataList.size

    inner class FollowingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.usernameFollower)
        private val image: ImageView = itemView.findViewById(R.id.imageFollower)
        private val follow: TextView = itemView.findViewById(R.id.followFollower)
        private val unfollow: TextView = itemView.findViewById(R.id.unfollowFollower)

        init {
            SharedPref.initialize(context)
            val followBy = SharedPref.getUserId().toString()

            userName.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val intent = Intent(context, UserProfile::class.java)
                intent.putExtra("userId", dataList.get(position).id)
                context.startActivity(intent)
            }

            unfollow.setOnClickListener{
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val userId = dataList?.get(position)!!.id
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://c-app.in/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)
                val request = toggleFollow(userId,followBy)
                val call = apiService.follow(request)

                call.enqueue(object : Callback<LikeResponse> {
                    override fun onResponse(
                        call: Call<LikeResponse>,
                        response: Response<LikeResponse>
                    ) {
                        val data = response.body()?.message
                        data?.let {
                            dataList.removeAt(position)
                            notifyDataSetChanged()
                            Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                    }
                })
            }
        }
        fun bind(
            data: Follower,
            context: Context
        ) {
            userName.text = data.username
            Glide.with(context)
                .load("https://c-app.in/uploads/register/${data.image}")
                .into(image)

            follow.visibility = View.GONE
            unfollow.visibility = View.VISIBLE

        }
    }
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
}