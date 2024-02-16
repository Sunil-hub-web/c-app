package com.net.capp

import SharedPref
import android.content.Context
import android.content.Intent
import android.util.Log
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

class FollowerAdapter(private val dataList: UserDataDetails?, private val dataList1: List<Follower>, private val context: Context) :
    RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder>() {

    private val itemClickListener: FollowerAdapter.OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.follower_item_view, parent, false)
        return FollowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowerViewHolder, position: Int) {
        val data = dataList
        val data1 = dataList1[position]
        holder.bind(data!!,data1,context)
    }

    override fun getItemCount(): Int = dataList1.size



    inner class FollowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                intent.putExtra("userId", dataList?.followers?.get(position)?.id.toString())
                context.startActivity(intent)
            }

            unfollow.setOnClickListener{
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val userId = dataList?.followers?.get(position)?.id.toString()
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
                            Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show()
                            follow.visibility = View.VISIBLE
                            unfollow.visibility = View.GONE
                        }
                    }
                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                    }
                })
            }

            follow.setOnClickListener{
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val userId = dataList?.followers?.get(position)?.id.toString()
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
                            follow.visibility = View.GONE
                            unfollow.visibility = View.VISIBLE
                            Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                    }
                })
            }

        }

        fun bind(
            data: UserDataDetails,
            data1: Follower,
            context: Context
        ) {
            userName.text = data1.username
            Glide.with(context)
                .load("https://c-app.in/uploads/register/${data1.image}")
                .into(image)

            for (item in data.followed){
                if (data1.id.equals(item.id)){
                    Log.d("test17", "${data1.id},${item.id}")
                    follow.visibility = View.GONE
                    unfollow.visibility = View.VISIBLE
                }
            }
        }
    }

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

}