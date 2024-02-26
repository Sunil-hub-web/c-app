package com.net.capp

import SharedPref
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
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
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.annotations.NonNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.net.URL


class HomeAdapter(private var dataList: MutableList<dataHomeItem>, private val context: Context) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private val itemClickListener: HomeAdapter.OnClickListener?= null
    private var currentPlayingPlayer: ExoPlayer? = null
    private var currentlyPlayingPosition: Int = -1
    private var Player: ExoPlayer? = null

    var DURATION: Long = 500
    private var on_attach = true
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_home, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data,context)

        setAnimation(holder.itemView, position);
    }

    override fun onViewDetachedFromWindow(holder: HomeViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val adVolume: ImageView = holder.itemView.findViewById(R.id.adVolume)

        val position = holder.adapterPosition
        if (currentPlayingPlayer?.isPlaying == true){
            currentPlayingPlayer?.pause()
        }
        if (Player?.isPlaying == true){
            Player?.volume = 0f
            adVolume.setImageResource(R.drawable.volume_mute)
        }
    }

    override fun getItemCount(): Int = dataList.size

    private fun setAnimation(itemView: View, i: Int) {
        var i = i
        if (!on_attach) {
            i = -1
        }
        val isNotFirstItem = i == -1
        i++
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 0.5f, 1.0f)
        ObjectAnimator.ofFloat(itemView, "alpha", 0f).start()
        animator.startDelay = if (isNotFirstItem) DURATION / 1 else i * DURATION / 2
        animator.duration = 2000
        animatorSet.play(animator)
        animator.start()
    }

    override fun onAttachedToRecyclerView(@NonNull recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                Log.d(TAG, "onScrollStateChanged: Called $newState")
                on_attach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
    }

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val userName: TextView = itemView.findViewById(R.id.usernameHome)
        private val follow: TextView = itemView.findViewById(R.id.followHome)
        private val finalDate: TextView = itemView.findViewById(R.id.finalDateHome)
        private val comment: TextView = itemView.findViewById(R.id.commentHome)
        private val reactions: TextView = itemView.findViewById(R.id.reactions)
        private val img: ImageView = itemView.findViewById(R.id.imageHome)
        private val img2: ImageView = itemView.findViewById(R.id.imageHome2)
        private val img3: ImageView = itemView.findViewById(R.id.imageHome3)
        private val img4: ImageView = itemView.findViewById(R.id.imageHome4)
        private val img4th: ConstraintLayout = itemView.findViewById(R.id.image4thHome)
        private val imgCount: TextView = itemView.findViewById(R.id.imageCountHome)
        private val imgCountLayout: ConstraintLayout = itemView.findViewById(R.id.imageCountLayoutHome)
        private val imagesHome: LinearLayout = itemView.findViewById(R.id.imagesHome)
        private val imageHomeMain: ImageView = itemView.findViewById(R.id.imageHomeMain)

        private val userImage: ImageView = itemView.findViewById(R.id.userImageHome)
        private val commentIcon: LinearLayout = itemView.findViewById(R.id.commentIcon)
        private val shareIcon: LinearLayout = itemView.findViewById(R.id.shareIcon)
        private val likeIcon: LinearLayout = itemView.findViewById(R.id.likeIcon)
        private val likeImageIcon: ImageView = itemView.findViewById(R.id.likeHome)

        private val downloadIcon: LinearLayout = itemView.findViewById(R.id.downloadIcon)
        private val views: TextView = itemView.findViewById(R.id.views)
        private val content: LinearLayout = itemView.findViewById(R.id.contentHome)
        private val user: LinearLayout = itemView.findViewById(R.id.userProfile)
        private val optionMenu: ImageView = itemView.findViewById(R.id.optionMenuHome)
        private val videoView: PlayerView = itemView.findViewById(R.id.videoViewHome)
        private val videoThumbnail : ImageView= itemView.findViewById(R.id.videoThumbnailHome)
        private val videoIcon: ImageView= itemView.findViewById(R.id.videoLogoHome)

        private val ad: ImageView = itemView.findViewById(R.id.adHome)
        private val adVolume: ImageView = itemView.findViewById(R.id.adVolume)

        private val adVideo: PlayerView = itemView.findViewById(R.id.adVideo)

        private val adView: CardView = itemView.findViewById(R.id.adView)

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
                            Log.d("successa", data.toString())
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
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }

                if (dataList.get(position).userid == userId){
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
                else{
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

            }

            content.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val intent = Intent(context, NewsDetailsActivity::class.java)
                intent.putExtra("userId", dataList.get(position).user)
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

            videoView.setOnClickListener {
                val position = adapterPosition
                if (position != currentlyPlayingPosition) {
                    // Play the new video and pause the currently playing video
                    playVideo(position)
                } else {
                    // Toggle play/pause for the currently playing video
//                    togglePlayback()
                }
            }
        }

        private fun initializePlayer(videoUrl: String) {
            if (currentPlayingPlayer == null) {
                currentPlayingPlayer = ExoPlayer.Builder(context).build()
                videoView.player = currentPlayingPlayer
            }
            Log.d("videoUrl", videoUrl)
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(videoUrl))
                .setMimeType(MimeTypes.APPLICATION_MP4)
                .build()

            val mediaSource = ProgressiveMediaSource.Factory(
                DefaultDataSource.Factory(context)
            ).createMediaSource(mediaItem)

            currentPlayingPlayer?.prepare(mediaSource)
            currentPlayingPlayer?.playWhenReady = true
            currentPlayingPlayer?.play()
        }

        fun bind(
            dataSet: dataHomeItem,
            context: Context
        ) {
            SharedPref.initialize(context)
            val userId = SharedPref.getUserId().toString()

            imageHomeMain.scaleType = ImageView.ScaleType.FIT_CENTER

            userName.text = dataSet.user
            finalDate.text = dataSet.finaldate
            comment.text = "${dataSet.comment} "
            reactions.text = "${dataSet.totallike}"
            views.text = "${dataSet.totalViewCount} Views"
            if (dataSet.like == "1"){
                likeImageIcon.setImageResource(R.drawable.liked)
            }
            else{
                likeImageIcon.setImageResource(R.drawable.heart)
            }


            if (dataSet.advertise == null){
                adView.visibility = View.GONE
            }
            else{
                ad.visibility = View.GONE
                adView.visibility = View.VISIBLE
                if (dataSet.advertise.video != null){
                    adVideo.visibility = View.VISIBLE
                    adVolume.visibility = View.VISIBLE
                    Player= ExoPlayer.Builder(context).build()
                    adVideo.player = Player
                    val mediaItem = MediaItem.Builder()
                        .setUri("https://c-app.in/uploads/register/${dataSet.advertise.video}")
                        .setMimeType(MimeTypes.APPLICATION_MP4)
                        .build()

                    val mediaSource = ProgressiveMediaSource.Factory(
                        DefaultDataSource.Factory(context)
                    ).createMediaSource(mediaItem)
                    Player?.setMediaItem(mediaItem)
                    Player?.prepare()
                    Player?.play()
                    Player?.volume = 0f

                    adVolume.setOnClickListener {
                        if (Player?.volume == 0f){
                            Player?.volume = 1f
                            adVolume.setImageResource(R.drawable.volume_unmute)
                        }
                        else{
                            Player?.volume = 0f
                            adVolume.setImageResource(R.drawable.volume_mute)
                        }
                    }
                }
                else{
                    adVideo.visibility = View.GONE
                    adVolume.visibility = View.GONE
                    ad.visibility = View.VISIBLE
                    Glide.with(context)
                        .load("https://c-app.in/uploads/register/${dataSet.advertise.image}")
                        .into(ad)
                }

            }

            for (item in dataSet.isFollowed){
                if(userId == item){
                    follow.visibility = View.GONE
                }
                else{
                    follow.visibility = View.VISIBLE
                    follow.setText("Follow")
                }
            }

            Glide.with(context)
                .load("https://c-app.in/uploads/register/${dataSet.userimage}")
                .into(userImage)

            userImage.setOnClickListener {
                val intent = Intent(context, ImageViewer::class.java)
                intent.putExtra("image","https://c-app.in/uploads/register/${dataSet.userimage}")
                context.startActivity(intent)
            }

            if (dataSet.video == null){
                videoView.visibility = View.GONE
                videoThumbnail.visibility = View.GONE
                videoIcon.visibility = View.GONE
                imageHomeMain.scaleType = ImageView.ScaleType.FIT_CENTER
                imageHomeMain.visibility = View.VISIBLE

                if (dataSet.image.size>1){
                    imageHomeMain.visibility = View.GONE
                    imageHomeMain.scaleType = ImageView.ScaleType.FIT_CENTER
                    imagesHome.visibility = View.VISIBLE
                    img2.visibility = View.VISIBLE
                    Glide.with(context)
                        .load("https://c-app.in/uploads/register/${dataSet.image.get(0)}")
                        .into(img)
                    Glide.with(context)
                        .load("https://c-app.in/uploads/register/${dataSet.image.get(1)}")
                        .into(img2)
                    if (dataSet.image.size>2){
                        img3.visibility = View.VISIBLE
                        Glide.with(context)
                            .load("https://c-app.in/uploads/register/${dataSet.image.get(2)}")
                            .into(img3)
                        if (dataSet.image.size>3){
                            img4th.visibility = View.VISIBLE
                            Glide.with(context)
                                .load("https://c-app.in/uploads/register/${dataSet.image.get(3)}")
                                .into(img4)
                            if (dataSet.image.size>4){
                                imgCountLayout.visibility = View.VISIBLE
                                imgCount.setText("+ ${dataSet.image.size-3}")
                            }
                            else{
                                imgCountLayout.visibility = View.GONE
                            }
                        }
                        else{
                            img4th.visibility = View.GONE
                            imgCountLayout.visibility = View.GONE
                        }
                    }
                    else{
                        img3.visibility = View.GONE
                        img4th.visibility = View.GONE
                        imgCountLayout.visibility = View.GONE
                    }

                }
                else{
                    imagesHome.visibility = View.GONE
                    imageHomeMain.scaleType = ImageView.ScaleType.FIT_CENTER

                    Glide.with(context)
                        .load("https://c-app.in/uploads/register/${dataSet.image.get(0)}")
                        .into(imageHomeMain)
                    videoView.visibility = View.GONE
                }
            }
            else{
                imageHomeMain.visibility = View.GONE
                imageHomeMain.scaleType = ImageView.ScaleType.FIT_CENTER

                imagesHome.visibility = View.GONE
                videoView.visibility = View.VISIBLE
                val videoUrl = "https://c-app.in/uploads/register/${dataSet.video}"

                videoThumbnail.visibility = View.VISIBLE
                videoIcon.visibility = View.VISIBLE

                Glide.with(context)
                    .load(videoUrl)
                    .into(videoThumbnail)
                videoView.setDefaultArtwork(videoThumbnail.drawable)

                videoThumbnail.setOnClickListener {
                    val position = adapterPosition
                    if (position != currentlyPlayingPosition) {
                        videoThumbnail.visibility = View.GONE
                        videoIcon.visibility = View.GONE
                        playVideo(position)
                    } else {
                        togglePlayback()
                    }
                }

                val position = adapterPosition
                if (position == currentlyPlayingPosition) {
                    initializePlayer(videoUrl)
                    videoThumbnail.visibility = View.GONE
                    videoIcon.visibility = View.GONE
                } else {
                    releasePlayer()
                }
                imageHomeMain.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }
        private fun playVideo(position: Int) {
            releasePlayer()
            currentlyPlayingPosition = position
            notifyItemChanged(position)
            videoThumbnail.visibility = View.GONE
            videoIcon.visibility = View.GONE
        }

    }

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

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
            null
        }
    }

    private fun togglePlayback() {
        if (currentPlayingPlayer?.playWhenReady == true) {
            currentPlayingPlayer?.playWhenReady = false
        } else {
            currentPlayingPlayer?.playWhenReady = true
        }
    }

    private fun releasePlayer() {
        currentPlayingPlayer?.release()
        currentPlayingPlayer = null
    }

}
