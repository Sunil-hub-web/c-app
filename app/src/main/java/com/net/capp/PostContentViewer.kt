package com.net.capp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostContentViewer : AppCompatActivity() {

    lateinit var username: TextView
    lateinit var finaldate: TextView
    lateinit var group: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var comment: TextView
    lateinit var userImage: ImageView
    lateinit var videoView: PlayerView
    var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_content_viewer)
        val toolbar = findViewById<Toolbar>(R.id.contentViewer_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(null)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        username = findViewById(R.id.usernameViewer)
        finaldate = findViewById(R.id.finalDateViewer)
        group = findViewById(R.id.groupViewer)
        userImage = findViewById(R.id.userImageViewer)
        recyclerView = findViewById(R.id.recyclerViewViewer)
        comment = findViewById(R.id.commentViewer)
        videoView = findViewById(R.id.videoViewer)

        loadContent()
    }

    private fun loadContent(){
        val progressDialog = Dialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

        val newsId = intent.getStringExtra("newsId").toString()
        val userId = intent.getStringExtra("userId").toString()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getNewsDetail(newsId,userId)
        call.enqueue(object : Callback<data_class_NewsDetail> {
            override fun onResponse(
                call: Call<data_class_NewsDetail>,
                response: Response<data_class_NewsDetail>
            ) {
                if (response.isSuccessful){
                    val body = response.body()?.resp!!
                    username.setText("${body.user} .")
                    finaldate.setText(body.finaldate)
                    group.setText(body.group)
                    comment.setText(body.comment)
                    Glide.with(this@PostContentViewer)
                        .load("https://c-app.in/uploads/register/${body.userimage}")
                        .into(userImage)
                    if (body.video != null){
                        loadVideo(body.video.toString())
                    }
                    Log.d("test",body.image.toString())
                    val adapter = ContentViewerAdapter(body.image, this@PostContentViewer)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@PostContentViewer)
                    progressDialog.dismiss()
                }
            }
            override fun onFailure(call: Call<data_class_NewsDetail>, t: Throwable) {
            }
        })
    }
    private fun loadVideo(videoUrl: String){
        player= ExoPlayer.Builder(this).build()
        videoView.player = player
        Log.d("videoUrl", videoUrl)
        val mediaItem = MediaItem.Builder()
            .setUri("https://c-app.in/uploads/register/${videoUrl}")
            .setMimeType(MimeTypes.APPLICATION_MP4)
            .build()

        videoView.visibility = View.VISIBLE

        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(this)
        ).createMediaSource(mediaItem)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        videoView.setOnClickListener {
            player?.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
        player?.release()
        player = null
    }

}