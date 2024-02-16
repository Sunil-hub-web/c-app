package com.net.capp

import SharedPref
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
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

class SearchPostAdapter(private val dataList: List<searchItem>, private val context: Context) :
    RecyclerView.Adapter<SearchPostAdapter.SearchViewHolder>() {

    private val itemClickListener: SearchPostAdapter.OnClickListener?= null
    private var currentPlayingPlayer: ExoPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_home, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data,context)
    }

    override fun getItemCount(): Int = dataList.size

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.usernameHome)
        private val follow: TextView = itemView.findViewById(R.id.followHome)
        private val finalDate: TextView = itemView.findViewById(R.id.finalDateHome)
        private val comment: TextView = itemView.findViewById(R.id.commentHome)
        private val reactions: TextView = itemView.findViewById(R.id.reactions)
        private val image: ImageView = itemView.findViewById(R.id.imageHomeMain)
        private val userImage: ImageView = itemView.findViewById(R.id.userImageHome)
        private val commentIcon: LinearLayout = itemView.findViewById(R.id.commentIcon)
        private val shareIcon: LinearLayout = itemView.findViewById(R.id.shareIcon)
        private val likeIcon: LinearLayout = itemView.findViewById(R.id.likeIcon)
        private val downloadIcon: LinearLayout = itemView.findViewById(R.id.downloadIcon)
        private val views: TextView = itemView.findViewById(R.id.views)
        private val content: LinearLayout = itemView.findViewById(R.id.contentHome)
        private val user: LinearLayout = itemView.findViewById(R.id.userProfile)
        private val optionMenu: ImageView = itemView.findViewById(R.id.optionMenuHome)
        private val imagesHome: LinearLayout = itemView.findViewById(R.id.imagesHome)
        private val imageHomeMain: ImageView = itemView.findViewById(R.id.imageHomeMain)
        private val videoView: PlayerView = itemView.findViewById(R.id.videoViewHome)
//        private val videoThumbnail : ImageView= itemView.findViewById(R.id.videoThumbnailHome)
//        private val videoIcon: ImageView= itemView.findViewById(R.id.videoLogoHome)

        init {
            SharedPref.initialize(context)
            val userId = SharedPref.getUserId().toString()

            follow.setOnClickListener {
                val followedBy = SharedPref.getUserId().toString()
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val userId = dataList.get(position).userid
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
                            Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show()
                            follow.visibility = View.GONE
                        }
                    }
                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                    }
                })
            }

            user.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val intent = Intent(context, UserProfile::class.java)
                intent.putExtra("userId", dataList.get(position).userid)
                context.startActivity(intent)
            }

            optionMenu.setOnClickListener {
                val popup = PopupMenu(context, optionMenu)
                popup.menuInflater.inflate(R.menu.option_menu_home, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.report_user -> {
                            Toast.makeText(context, "User Reported", Toast.LENGTH_SHORT).show()
                            true
                        }

                        R.id.report_content -> {
                            Toast.makeText(context, "Content Reported", Toast.LENGTH_SHORT).show()
                            true
                        }

                        R.id.block_user -> {
                            Toast.makeText(context, "Blocked", Toast.LENGTH_SHORT).show()
                            true
                        }

                        else -> false
                    }
                }
                popup.show()
            }

            content.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }

                val intent = Intent(context, NewsDetailsActivity::class.java)
                intent.putExtra("userId", dataList.get(position).userid)
                intent.putExtra("newsId", dataList.get(position).id)
                context.startActivity(intent)
            }
            likeIcon.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val newsId = dataList.get(position).id
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://c-app.in/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)
                val request = postlike(newsId,userId)
                val response = apiService.postlike(request)
                notifyItemChanged(position)
                response.enqueue(object: Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                        val list = response.body()?.data?.totallike!!
                        dataList[position].totallike = list.toInt()
                        notifyItemChanged(position)
                    }
                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                    }
                })
            }

            commentIcon.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val intent = Intent(context, CommentActivity::class.java)
                intent.putExtra("id", dataList.get(position).id)
                context.startActivity(intent)
            }

            shareIcon.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val comment = "${dataList[position].comment.take(110)}... \n\nDownload C App now \nhttps://play.google.com/store/apps/details?id=com.net.capp&pli=1"
                if (dataList[position].video==null){
                    Toast.makeText(context, "Sharing.. Please wait", Toast.LENGTH_SHORT).show()
                    val imageUrls = dataList[position].image
                    if (imageUrls == "white.jpg"){
                        val intent = Intent()
                        intent.setAction(Intent.ACTION_SEND_MULTIPLE)
                        intent.putExtra(Intent.EXTRA_TEXT,comment)
                        intent.setType("text/plane")
                        context.startActivity(Intent.createChooser(intent, "Share News"))
                    }
                    else{
                        shareImageAndText("https://c-app.in/uploads/register/${imageUrls}", comment)
                    }
                }
                else{
                    Toast.makeText(context, "Sharing.. Please wait", Toast.LENGTH_SHORT).show()
                    val videoUrl = "https://c-app.in/uploads/register/${dataList[position].video}"
                    val comment = "${dataList[position].comment.take(110)}..."
                    shareVideoThumbnailAndText(videoUrl, comment)
                }
            }

            downloadIcon.setOnClickListener {
                Toast.makeText(context, "Downloading..", Toast.LENGTH_SHORT).show()
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                for (item in dataList.get(position).image){
                    val downloader = AndroidDownloader(context)
                    downloader.downloadFile("https://c-app.in/uploads/register/${item}")
                }
            }

        }

        fun bind(
            data: searchItem,
            context: Context
        ) {
            SharedPref.initialize(context)
            val userId = SharedPref.getUserId().toString()

            userName.text = data.user
            finalDate.text = data.finaldate
            comment.text = data.comment
            reactions.text = "${data.totallike}"
            views.text = "${data.totalViewCount} Views"

            for (item in data.isFollowed){
                if(userId == item){
                    follow.visibility = View.GONE
                }
                else{
                    follow.visibility = View.VISIBLE
                    follow.setText("Follow")
                }
            }

            Glide.with(context)
                .load("https://c-app.in/uploads/register/${data.userimage}")
                .into(userImage)
            if (data.video == null){
                videoView.visibility = View.GONE
                image.visibility = View.VISIBLE
                Glide.with(context)
                    .load("https://c-app.in/uploads/register/${data.image}")
                    .into(image)
            }
            else{
                image.visibility = View.GONE
                imagesHome.visibility = View.GONE
                videoView.visibility = View.VISIBLE

                currentPlayingPlayer = ExoPlayer.Builder(context).build()
                videoView.player = currentPlayingPlayer

                val videoUrl = "https://c-app.in/uploads/register/${data.video}"

                Log.d("videoUrl", videoUrl)
                val mediaItem = MediaItem.Builder()
                    .setUri(videoUrl)
                    .setMimeType(MimeTypes.APPLICATION_MP4)
                    .build()

                val mediaSource = ProgressiveMediaSource.Factory(
                    DefaultDataSource.Factory(context)
                ).createMediaSource(mediaItem)

                currentPlayingPlayer!!.setMediaItem(mediaItem)
                currentPlayingPlayer!!.prepare()
//                currentPlayingPlayer!!.playWhenReady = true

                videoView.setOnClickListener {
                    currentPlayingPlayer!!.play()
                }

//                videoIcon.visibility = View.GONE
//                videoThumbnail.visibility = View.GONE

            }
        }
    }
    interface OnClickListener {
        fun onItemClick(position: Int)
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
//                val contentUri = FileProvider.getUriForFile(context, "${"com.example.themitra"}.provider", outputFile)
//
//                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
//                shareIntent.putExtra(Intent.EXTRA_TEXT, comment)
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
//                context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    private fun shareVideoThumbnailAndText(videoUrl: String, text: String) {
        val task = object : AsyncTask<Void, Void, Uri?>() {
            override fun doInBackground(vararg params: Void?): Uri? {
                val thumbnailBitmap = getVideoThumbnail(videoUrl)
                return if (thumbnailBitmap != null) {
                    val imagePath = MediaStore.Images.Media.insertImage(
                        context.contentResolver,
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
                        type = "image/*"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Video Thumbnail and Text"))
                } else {
                    Toast.makeText(context, "Failed to share video thumbnail and text", Toast.LENGTH_SHORT).show()
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
                        context.contentResolver,
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
                    context.startActivity(Intent.createChooser(shareIntent, "Share Image and Text"))
                } else {
                    Toast.makeText(context, "Failed to share image and text", Toast.LENGTH_SHORT).show()
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
}