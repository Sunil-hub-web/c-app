package com.net.capp

import SharedPref
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfile : AppCompatActivity() {

    lateinit var locationTextView: TextView
    lateinit var usernameTextView: TextView
    lateinit var aboutTextView: TextView
//    lateinit var tabLayout: TabLayout
    lateinit var postCount: TextView
    lateinit var followersCount: TextView
    lateinit var followingCount: TextView
    lateinit var follow: TextView
    lateinit var unfollow: TextView
    lateinit var userImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val userId = intent.getStringExtra("userId").toString()

        loadNews(userId)
        val toolbar = findViewById<Toolbar>(R.id.userProfile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        SharedPref.initialize(this)
        val myUserId = SharedPref.getUserId().toString()



        locationTextView = findViewById(R.id.location_User)
        usernameTextView = findViewById(R.id.AccountUser)
        aboutTextView = findViewById(R.id.about_User)
        userImage = findViewById(R.id.userImage)

        follow = findViewById(R.id.followUser)
        unfollow = findViewById(R.id.unfollowUser)

        followersCount = findViewById(R.id.followersCountUser)
        followingCount = findViewById(R.id.followingCountUser)
        postCount = findViewById(R.id.postCountUser)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getuserDetails(userId)

        Log.d("useriddata",userId)

        call.enqueue(object : Callback<UserData> {
            override fun onResponse(
                call: Call<UserData>,
                response: Response<UserData>
            ) {
                val data = response.body()?.data
                val userDataDetails = response.body()?.data?.user
                Glide.with(this@UserProfile)
                    .load("${userDataDetails?.image}")
                    .into(userImage)

                userImage.setOnClickListener {
                    val intent = Intent(this@UserProfile, ImageViewer::class.java)
                    intent.putExtra("image","${userDataDetails?.image}")
                    startActivity(intent)
                }
                data?.let {
                    postCount.setText(data.postcount.toString())
                    followingCount.setText(data.followedCount.toString())
                    followersCount.setText(data.followersCount.toString())
                    usernameTextView.setText(data.user.username)
                    locationTextView.setText(data.user.city+", "+data.user.state)
                    aboutTextView.setText(data.user.about)

                    for (item in data.followers){
                        if (myUserId == item.id){
                            unfollow.visibility = View.VISIBLE
                            follow.visibility = View.GONE
                        }
                        else{
                            follow.visibility = View.VISIBLE
                            unfollow.visibility = View.GONE
                        }
                    }

                    follow.setOnClickListener {
                        val followedBy = SharedPref.getUserId().toString()
                        val userId = data.user.id
                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://c-app.in/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        val apiService = retrofit.create(ApiService::class.java)
                        val request = toggleFollow(userId,followedBy)
                        val call = apiService.follow(request)

                        call.enqueue(object : Callback<LikeResponse> {
                            override fun onResponse(
                                call: Call<LikeResponse>,
                                response: Response<LikeResponse>
                            ) {
                                val data = response.body()?.message
                                data?.let {
                                    Log.d("successa", data.toString())
                                    Toast.makeText(this@UserProfile, data.toString(), Toast.LENGTH_SHORT).show()
                                    follow.visibility = View.GONE
                                    unfollow.visibility = View.VISIBLE
                                }
                            }
                            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                            }
                        })
                    }
                    unfollow.setOnClickListener {
                        val followedBy = SharedPref.getUserId().toString()
                        val userId = data.user.id
                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://c-app.in/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        val apiService = retrofit.create(ApiService::class.java)
                        val request = toggleFollow(userId,followedBy)
                        val call = apiService.follow(request)

                        call.enqueue(object : Callback<LikeResponse> {
                            override fun onResponse(
                                call: Call<LikeResponse>,
                                response: Response<LikeResponse>
                            ) {
                                val data = response.body()?.message
                                data?.let {
                                    Log.d("successa", data.toString())
                                    Toast.makeText(this@UserProfile, data.toString(), Toast.LENGTH_SHORT).show()
                                    unfollow.visibility = View.GONE
                                    follow.visibility = View.VISIBLE
                                }
                            }
                            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                            }
                        })
                    }

                }
            }
            override fun onFailure(call: Call<UserData>, t: Throwable) {
            }
        })
    }

    private fun loadNews(userId: String){
        val progressDialog = Dialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getUserNewsData(userId)

        call.enqueue(object : Callback<data_class_NewsDetail> {
            override fun onResponse(
                call: Call<data_class_NewsDetail>,
                response: Response<data_class_NewsDetail>
            ) {
                if (response.isSuccessful){
                    val body = response.body()?.data!!
                    val recyclerView = findViewById<RecyclerView>(R.id.recyclerUser)

                    val adapter = suggestionAdapter(body, this@UserProfile)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@UserProfile)
                    progressDialog.dismiss()
                }
            }
            override fun onFailure(call: Call<data_class_NewsDetail>, t: Throwable) {
            }
        })
    }
}