package com.net.capp

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class NewsDetailsActivity : AppCompatActivity() {
    lateinit var username : TextView
    lateinit var finaldate : TextView
    lateinit var comment : TextView
    lateinit var reactions : TextView
    lateinit var views : TextView
    lateinit var like : LinearLayout
    lateinit var commentPost : LinearLayout
    lateinit var content : LinearLayout
    lateinit var imagesContent : LinearLayout
    lateinit var share : LinearLayout
    lateinit var download : LinearLayout
    lateinit var imagesLayout: LinearLayout
    lateinit var image: ImageView
    lateinit var image0: ImageView
    lateinit var image2: ImageView
    lateinit var image3: ImageView
    lateinit var image4: ImageView
    lateinit var image4th: ConstraintLayout
    lateinit var imageCount: TextView
    lateinit var imageCountLayout: ConstraintLayout
    lateinit var userImage: ImageView
    lateinit var videoView: PlayerView
    var player: ExoPlayer? = null
    var newsId: String? = null
    var userId: String? = null
    lateinit var imagesUrls: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)

        val toolbar = findViewById<Toolbar>(R.id.newsDetail_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        SharedPref.initialize(this)
        val Id = SharedPref.getUserId().toString()

        loadContent()

        username = findViewById(R.id.usernameNews)
        finaldate = findViewById(R.id.finalDateNews)
        comment = findViewById(R.id.commentNews)
        reactions = findViewById(R.id.reactionsNews)
        image0 = findViewById(R.id.image0MainNews)
        image = findViewById(R.id.imageNews)
        imagesLayout = findViewById(R.id.imagesNews)
        image2 = findViewById(R.id.imageNews2)
        image3 = findViewById(R.id.imageNews3)
        image4 = findViewById(R.id.imageNews4)
        image4th = findViewById(R.id.image4thNews)
        imageCount = findViewById(R.id.imageCountNews)
        imageCountLayout = findViewById(R.id.imageCountLayoutNews)
        imagesContent = findViewById(R.id.imagesNews)
        userImage = findViewById(R.id.userImageNews)
        views = findViewById(R.id.viewsNews)
        share = findViewById(R.id.shareIconNews)
        download = findViewById(R.id.downloadIconNews)
        commentPost = findViewById(R.id.commentIconNews)
        like = findViewById(R.id.likeIconNews)
        videoView = findViewById(R.id.videoNewsDetail)
        content = findViewById(R.id.contentNews)

        content.setOnClickListener {
            val intent = Intent(this, PostContentViewer::class.java)
            intent.putExtra("newsId", newsId)
            intent.putExtra("userId", userId)
            player?.stop()
            player?.release()
            player = null
            startActivity(intent)
        }

        like.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://c-app.in/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)
            val request = postlike(newsId!!,Id)
            val response = apiService.postlike(request)
            response.enqueue(object: Callback<LikeResponse> {
                override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                    val list = response.body()?.data?.totallike?.toInt()
                    reactions.setText("${list} Reactions")
                }
                override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                }
            })
        }

        commentPost.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("id", newsId)
            startActivity(intent)
        }



        download.setOnClickListener {
            Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show()
            for (item in imagesUrls){
                val downloader = AndroidDownloader(this)
                downloader.downloadFile("https://c-app.in/uploads/register/${item}")
            }
        }
    }

    private fun loadContent(){
        val progressDialog = Dialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

        newsId = intent.getStringExtra("newsId").toString()
        userId = intent.getStringExtra("userId").toString()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getNewsDetail(newsId!!, userId!!)
        call.enqueue(object : Callback<data_class_NewsDetail> {
            override fun onResponse(
                call: Call<data_class_NewsDetail>,
                response: Response<data_class_NewsDetail>
            ) {
                if (response.isSuccessful){
                    val body = response.body()?.data!!.subList(0,50)
                    val body1 = response.body()?.resp!!
                    username.setText(body1.user)
                    finaldate.setText(body1.finaldate)
                    comment.setText(body1.comment)
                    views.setText("${body1.totalViewCount} Views")

                    share.setOnClickListener {
                        val comment = "${body1.comment.take(110)}... \n\nDownload C App now \nhttps://play.google.com/store/apps/details?id=com.net.capp&pli=1"
                        if (body1.video==null){
                            Toast.makeText(this@NewsDetailsActivity, "Sharing.. Please wait", Toast.LENGTH_SHORT).show()
                            val imageUrls = body1.image.get(0)
                            if (imageUrls == "white.jpg"){
                                val intent = Intent()
                                intent.setAction(Intent.ACTION_SEND_MULTIPLE)
                                intent.putExtra(Intent.EXTRA_TEXT,comment)
                                intent.setType("text/plane")
                                startActivity(Intent.createChooser(intent, "Share News"))
                            }
                            else{
                                shareImageAndText("https://c-app.in/uploads/register/${imageUrls}", comment)
                            }
                        }
                        else{
                            val videoUrl = "https://c-app.in/uploads/register/${body1.video}"
                            Toast.makeText(this@NewsDetailsActivity, "Sharing.. Please wait", Toast.LENGTH_SHORT).show()
                            val comment = "${body1.comment.take(110)}... \n\nWatch full video on C App. Download C App now\nhttps://play.google.com/store/apps/details?id=com.net.capp&pli=1"
                            shareVideoThumbnailAndText(videoUrl, comment)
                        }
                    }

                    image0.visibility = View.VISIBLE
                    imagesContent.visibility = View.GONE
                    Glide.with(this@NewsDetailsActivity)
                        .load("https://c-app.in/uploads/register/${body1.image.get(0)}")
                        .into(image0)
                    Glide.with(this@NewsDetailsActivity)
                        .load("https://c-app.in/uploads/register/${body1.userimage}")
                        .into(userImage)
                    reactions.setText("${body1.totallike} Reactions")

                    if (body1.video != null){
                        loadVideo(body1.video)
                    }
                    imagesUrls = body1.image
                    val recyclerView = findViewById<RecyclerView>(R.id.recyclerNewsDetail)
                    val adapter = suggestionAdapter(body, this@NewsDetailsActivity)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@NewsDetailsActivity)

                    if (body1.image.size == 1 && body1.image.get(0).equals("white.jpg")){
                        imagesLayout.visibility = View.GONE

                    }


                    if (body1.image.size>1){
                        image0.visibility = View.GONE
                        image2.visibility = View.VISIBLE
                        imagesContent.visibility = View.VISIBLE

                        Glide.with(this@NewsDetailsActivity)
                            .load("https://c-app.in/uploads/register/${body1.image.get(0)}")
                            .into(image)
                        Glide.with(this@NewsDetailsActivity)
                            .load("https://c-app.in/uploads/register/${body1.image.get(1)}")
                            .into(image2)
                        if (body1.image.size>2){
                            image3.visibility = View.VISIBLE
                            Glide.with(this@NewsDetailsActivity)
                                .load("https://c-app.in/uploads/register/${body1.image.get(2)}")
                                .into(image3)
                        }
                        if (body1.image.size>3){
                            image4th.visibility = View.VISIBLE
                            Glide.with(this@NewsDetailsActivity)
                                .load("https://c-app.in/uploads/register/${body1.image.get(3)}")
                                .into(image4)
                        }
                        if (body1.image.size>4){
                            imageCountLayout.visibility = View.VISIBLE
                            imageCount.setText("+ ${body1.image.size-3}")
                        }
                    }
                    progressDialog.dismiss()

                }
            }
            override fun onFailure(call: Call<data_class_NewsDetail>, t: Throwable) {
                Toast.makeText(this@NewsDetailsActivity, "Failed", Toast.LENGTH_SHORT).show()
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

        imagesContent.visibility = View.GONE
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

//    private fun downloadAndShareVideo(videoUrl: String, comment: String) {
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val url = URL(videoUrl)
//                val connection = url.openConnection() as HttpURLConnection
//                connection.connect()
//                val inputStream = connection.inputStream
//                val storageDir =
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                val fileName = "video.mp4"
//                val outputFile = File(storageDir, fileName)
//
//                FileOutputStream(outputFile).use { output ->
//                    val buffer = ByteArray(4 * 1024)
//                    var bytesRead: Int
//                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                        output.write(buffer, 0, bytesRead)
//                    }
//                    output.flush()
//                }
//
//                connection.disconnect()
//
//                val shareIntent = Intent(Intent.ACTION_SEND)
//                shareIntent.type = "video/*"
//
//                val contentUri = FileProvider.getUriForFile(this@NewsDetailsActivity, "${"com.example.themitra"}.provider", outputFile)
//
//                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
//                shareIntent.putExtra(Intent.EXTRA_TEXT, comment)
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
//                startActivity(Intent.createChooser(shareIntent, "Share Video"))
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.d("videoUrlEEE", e.toString())
//            }
//        }
//    }
private fun shareVideoThumbnailAndText(videoUrl: String, text: String) {
    val task = object : AsyncTask<Void, Void, Uri?>() {
        override fun doInBackground(vararg params: Void?): Uri? {
            val thumbnailBitmap = getVideoThumbnail(videoUrl)
            return if (thumbnailBitmap != null) {
                val imagePath = MediaStore.Images.Media.insertImage(
                    contentResolver,
                    thumbnailBitmap,
                    "Video Thumbnail",
                    null
                )
                Uri.parse(imagePath)
            } else {
                null
            }
        }

        override fun onPostExecute(result: Uri?) {
            super.onPostExecute(result)
            if (result != null) {
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, result)
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "image/*" // Set the MIME type to image/* for the thumbnail
                }
                startActivity(Intent.createChooser(shareIntent, "Share Video Thumbnail and Text"))
            } else {
                Toast.makeText(this@NewsDetailsActivity, "Failed to share video thumbnail and text", Toast.LENGTH_SHORT).show()
            }
        }
    }
    task.execute()
}

    private fun getVideoThumbnail(videoUrl: String): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoUrl, HashMap<String, String>())
            val bitmap = retriever.frameAtTime
            retriever.release()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun shareImageAndText(imageUrl: String, text: String) {
        val task = object : AsyncTask<Void, Void, Uri?>() {
            override fun doInBackground(vararg params: Void?): Uri? {
                val bitmap = getBitmapFromUrl(imageUrl)
                return if (bitmap != null) {
                    val imagePath = MediaStore.Images.Media.insertImage(
                        contentResolver,
                        bitmap,
                        "Image Description",
                        null
                    )
                    Uri.parse(imagePath)
                } else {
                    null
                }
            }
            override fun onPostExecute(result: Uri?) {
                super.onPostExecute(result)
                if (result != null) {
                    val shareIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, result)
                        putExtra(Intent.EXTRA_TEXT, text)
                        type = "image/*"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share Image and Text"))
                } else {
                    Toast.makeText(this@NewsDetailsActivity, "Failed to share image and text", Toast.LENGTH_SHORT).show()
                }
            }
        }
        task.execute()
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 30000
            connection.readTimeout = 30000
            connection.instanceFollowRedirects = true
            val input = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(input)
            connection.disconnect()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("img124", e.toString())
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
        player?.release()
        player = null
    }

}