package com.net.capp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FollowingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)
        val toolbar = findViewById<Toolbar>(R.id.following_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        SharedPref.initialize(this)
        val userId = SharedPref.getUserId().toString()

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
        val call = apiService.getuserDetails(userId)

        call.enqueue(object : Callback<UserData> {
            override fun onResponse(
                call: Call<UserData>,
                response: Response<UserData>
            ) {
                val data = response.body()?.data?.followed!!
                data?.let {
                    val recyclerView = findViewById<RecyclerView>(R.id.recyclerFollowing)

                    val dividerItemDecoration = DividerItemDecoration(this@FollowingActivity, LinearLayoutManager.VERTICAL)
                    dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this@FollowingActivity, R.drawable.line)!!)
                    recyclerView.addItemDecoration(dividerItemDecoration)

                    val adapter = FollowingAdapter(data,this@FollowingActivity)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@FollowingActivity)
                    progressDialog.dismiss()
                }
            }
            override fun onFailure(call: Call<UserData>, t: Throwable) {
            }
        })
    }
}