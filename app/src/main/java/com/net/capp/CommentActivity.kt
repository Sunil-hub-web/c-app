package com.net.capp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.emoji2.text.EmojiCompat
import androidx.emoji2.widget.EmojiEditText
//import androidx.emoji2.bundled.BundledEmojiCompatConfig
//import androidx.emoji2.text.EmojiCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommentActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        val toolbar = findViewById<Toolbar>(R.id.comment_toolbar)
        recyclerView = findViewById(R.id.recyclerViewComment)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        SharedPref.initialize(this)
        val userId = SharedPref.getUserId().toString()

        val sendButton = findViewById<CardView>(R.id.sendComment)
        val commentEditText = findViewById<EmojiEditText>(R.id.commentEditText)

        val newsId = intent.getStringExtra("id").toString()
        loadComments(newsId)

        sendButton.setOnClickListener {
            val progressDialog = Dialog(this)
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            progressDialog.setContentView(R.layout.progress_dialog)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://c-app.in/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val commentText = EmojiCompat.get().process(commentEditText.text).toString()
            val apiService = retrofit.create(ApiService::class.java)
            val request = postComment(newsId,userId,commentText)
            val response = apiService.postcomment(request)

            response.enqueue(object: Callback<LikeResponse> {
                override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                    commentEditText.text?.clear()
                    val body = response.body().toString()
                    loadComments(newsId)
                    progressDialog.dismiss()
                }
                override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                }
            })
        }
    }

    fun loadComments(newsId: String){
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
        val response = apiService.getComments(newsId)

        response.enqueue(object: Callback<commentData> {
            override fun onResponse(call: Call<commentData>, response: Response<commentData>) {
                val body = response.body()?.resp!!
                val adapter = CommentAdapter(body, this@CommentActivity)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this@CommentActivity)
                progressDialog.dismiss()
            }
            override fun onFailure(call: Call<commentData>, t: Throwable) {
            }
        })
    }
}