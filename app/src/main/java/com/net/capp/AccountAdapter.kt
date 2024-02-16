package com.net.capp

import SharedPref
import android.app.Dialog
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
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

class AccountAdapter(private val dataList: MutableList<userNewsItem>, private val context: Context) :
    RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    private val itemClickListener: AccountAdapter.OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_usernews, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data,context)
    }

    override fun getItemCount(): Int = dataList.size

    inner class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.usernameAccount)
        private val finalDate: TextView = itemView.findViewById(R.id.finalDateAccount)
        private val group: TextView = itemView.findViewById(R.id.groupAccount)
        private val comment: TextView = itemView.findViewById(R.id.commentAccount)
        private val reactions: TextView = itemView.findViewById(R.id.reactionsAccount)
        private val image: ImageView = itemView.findViewById(R.id.imageAccount)
        private val image2: ImageView = itemView.findViewById(R.id.imageAccount2)
        private val image3: ImageView = itemView.findViewById(R.id.imageAccount3)
        private val image4: ImageView = itemView.findViewById(R.id.imageAccount4)
        private val image4th: ConstraintLayout = itemView.findViewById(R.id.image4th)
        private val imageCount: TextView = itemView.findViewById(R.id.imageCountAccount)
        private val imageCountLayout: ConstraintLayout = itemView.findViewById(R.id.imageCountLayout)

        private val videoIcon: ImageView = itemView.findViewById(R.id.videoLogoAccount)
        private val userImage: ImageView = itemView.findViewById(R.id.userImage)
        private val commentIcon: LinearLayout = itemView.findViewById(R.id.commentIconAccount)
        private val imageSection: LinearLayout = itemView.findViewById(R.id.imagesAccount)
        private val shareIcon: LinearLayout = itemView.findViewById(R.id.shareIconAccount)
        private val likeIcon: LinearLayout = itemView.findViewById(R.id.likeIconAccount)
        private val like: ImageView = itemView.findViewById(R.id.likeAccount)
        private val downloadIcon: LinearLayout = itemView.findViewById(R.id.downloadIconAccount)
        private val optionMenu: ImageView = itemView.findViewById(R.id.optionMenuPost)
        private val videoThumbnail: ImageView = itemView.findViewById(R.id.videoAccount)
        private val content: LinearLayout = itemView.findViewById(R.id.contentAccount)

        init {
            SharedPref.initialize(context)
            val userId = SharedPref.getUserId().toString()

            content.setOnClickListener {
                val intent = Intent(context, PostContentViewer::class.java)
                intent.putExtra("newsId", dataList.get(position).id)
                intent.putExtra("userId", dataList.get(position).userid)
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
                response.enqueue(object: Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                        val list = response.body()?.data?.totallike!!
                        val like = response.body()?.data?.like!!
                        dataList[position].totallike = list.toInt()
                        dataList[position].like = like
                        notifyItemChanged(position)
                    }
                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                    }
                })
            }

            optionMenu.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }

                val popup = PopupMenu(context, optionMenu)
                popup.menuInflater.inflate(R.menu.option_menu_item, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.option_edit -> {
                            val intent = Intent(context, EditUserPostActivity::class.java)
                            intent.putExtra("comment", dataList.get(position).comment)
                            intent.putExtra("groupid", dataList.get(position).groupid)
                            intent.putExtra("id", dataList.get(position).id)
                            intent.putStringArrayListExtra("image", ArrayList(dataList.get(position).image))
                            context.startActivity(intent)
                            true
                        }
                        R.id.option_delete -> {
                            val progressDialog = Dialog(context)
                            progressDialog.setCancelable(false)
                            progressDialog.setCanceledOnTouchOutside(false)
                            progressDialog.show()
                            progressDialog.setContentView(R.layout.progress_dialog)

                            val retrofit = Retrofit.Builder()
                                .baseUrl("https://c-app.in/api/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()

                            val newsId = dataList.get(position).id

                            val apiService = retrofit.create(ApiService::class.java)
                            val request = newsId
                            val response = apiService.deleteNews(request)
                            response.enqueue(object: Callback<LikeResponse> {
                                override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                                    dataList.removeAt(position)
                                    notifyDataSetChanged()
                                    progressDialog.dismiss()
                                }
                                override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                                }
                            })
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
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
                    val imageUrls = dataList[position].image.get(0)
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
                    val comment = "${dataList[position].comment.take(110)}... \n\nWatch full video on C App. Download C App now\nhttps://play.google.com/store/apps/details?id=com.net.capp&pli=1"
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
            data: userNewsItem,
            context: Context
        ) {
            userName.text = data.user
            finalDate.text = data.finaldate
            group.text = data.group
            comment.text = data.comment
            reactions.text = "${data.totallike}"
            if (data.like == "1"){
                like.setImageResource(R.drawable.liked)
            }

            Glide.with(context)
                .load("https://c-app.in/uploads/register/${data.userimage}")
                .into(userImage)

            userImage.setOnClickListener {
                val intent = Intent(context, ImageViewer::class.java)
                intent.putExtra("image","https://c-app.in/uploads/register/${data.userimage}")
                context.startActivity(intent)
            }

            Glide.with(context)
                .load("https://c-app.in/uploads/register/${data.image.get(0)}")
                .into(image)

            if (data.image.size>1){
                image2.visibility = View.VISIBLE
                Glide.with(context)
                    .load("https://c-app.in/uploads/register/${data.image.get(1)}")
                    .into(image2)
                if (data.image.size>2){
                    image3.visibility = View.VISIBLE
                    Glide.with(context)
                        .load("https://c-app.in/uploads/register/${data.image.get(2)}")
                        .into(image3)
                }
                if (data.image.size>3){
                    image4th.visibility = View.VISIBLE
                    Glide.with(context)
                        .load("https://c-app.in/uploads/register/${data.image.get(3)}")
                        .into(image4)
                }
                if (data.image.size>4){
                    imageCountLayout.visibility = View.VISIBLE
                    imageCount.setText("+ ${data.image.size-3}")
                }
            }


            if (data.video != null){
                val videoUrl = "https://c-app.in/uploads/register/${data.video}"
                Log.d("videoUrl", videoUrl)
                val uri = Uri.parse(videoUrl)

                imageSection.visibility = View.GONE
                videoIcon.visibility = View.VISIBLE
                videoThumbnail.visibility = View.VISIBLE
                Glide.with(context)
                    .load(videoUrl)
                    .into(videoThumbnail)
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
                        type = "image/*" // Set the MIME type to image/* for the thumbnail
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