package com.net.capp

import android.app.Dialog
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EditUserPostActivity : AppCompatActivity() {
    lateinit var imagesEdit : LinearLayout
    lateinit var imagesEdit1to3 : LinearLayout
    lateinit var imagesEdit4to6 : LinearLayout

    lateinit var image0 : ImageView
    lateinit var image1 : ImageView
    lateinit var image2 : ImageView
    lateinit var image3 : ImageView
    lateinit var image4 : ImageView

    lateinit var img0 : ImageView
    lateinit var img1 : ImageView
    lateinit var img2 : ImageView
    lateinit var img3 : ImageView
    lateinit var img4 : ImageView
    lateinit var img5 : ImageView
    lateinit var img6 : ImageView
    lateinit var img: String
    var video: String = ""

    var player : ExoPlayer? = null
    lateinit var uriVideo: Uri

    lateinit var vid: PlayerView

    lateinit var submitPost : Button
    lateinit var editText: EditText
    lateinit var selectImage : LinearLayout
    lateinit var selectVideo : LinearLayout
    lateinit var selectThumbnail : LinearLayout
    lateinit var uri: List<Uri>

    val getCont = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
        imagesEdit1to3.visibility = View.VISIBLE
        imagesEdit4to6.visibility = View.VISIBLE

        uri = it!!
        if (uri.size>0){
            img0.setImageURI(uri.get(0))
            if (uri.size>1){
                img0.visibility = View.GONE
                img1.setImageURI(uri.get(0))
                img2.setImageURI(uri.get(1))
                if (uri.size>2){
                    img3.visibility = View.VISIBLE
                    img3.setImageURI(uri.get(2))
                    if (uri.size>3){
                        img4.setImageURI(uri.get(3))
                        if (uri.size>4){
                            img5.setImageURI(uri.get(4))
                            if (uri.size>5){
                                img6.setImageURI(uri.get(5))
                            }
                        }
                    }
                }
            }
            upload()
        }
    }

    val getVideoContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uriVideo = it!!
        vid.visibility = View.VISIBLE

        player = ExoPlayer.Builder(this@EditUserPostActivity).build()
        vid.player = player
        val mediaItem = MediaItem.Builder()
            .setUri(it)
            .setMimeType(MimeTypes.APPLICATION_MP4)
            .build()

        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(this@EditUserPostActivity)
        ).createMediaSource(mediaItem)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        vid.setOnClickListener {
            player?.play()
        }
        videoUpload()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_post)

        val toolbar = findViewById<Toolbar>(R.id.editPost_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        SharedPref.initialize(this)
        val userId = SharedPref.getUserId().toString()

        imagesEdit = findViewById(R.id.imagesEdit)
        imagesEdit1to3 = findViewById(R.id.editpostimages1to3)
        imagesEdit4to6 = findViewById(R.id.editpostimages4to6)

        image0 = findViewById(R.id.editimage)
        image1 = findViewById(R.id.imageEdit)
        image2 = findViewById(R.id.imageEdit2)
        image3 = findViewById(R.id.imageEdit3)
        image4 = findViewById(R.id.imageEdit4)

        img0 = findViewById(R.id.editpostImage0)
        img1 = findViewById(R.id.editpostImage)
        img2 = findViewById(R.id.editpostImage2)
        img3 = findViewById(R.id.editpostImage3)
        img4 = findViewById(R.id.editpostImage4)
        img5 = findViewById(R.id.editpostImage5)
        img6 = findViewById(R.id.editpostImage6)


        val img4th: ConstraintLayout = findViewById(R.id.image4thEdit)
        val imgCount: TextView = findViewById(R.id.imageCountEdit)
        val imgCountLayout: ConstraintLayout = findViewById(R.id.imageCountLayoutEdit)

        val text = intent.getStringExtra("comment").toString()
        val imageurl = intent.getStringArrayListExtra("image")!!
        val id = intent.getStringExtra("id").toString()
        val groupid = intent.getStringExtra("groupid").toString()
        val video = intent.getStringExtra("video").toString()

        img = imageurl.joinToString(",")
        Log.d("test3", img)

        Glide.with(this)
            .load("https://c-app.in/uploads/register/${imageurl.get(0)}")
            .into(image0)
        if (imageurl?.size!! >1){
            image0.visibility = View.GONE
            imagesEdit.visibility = View.VISIBLE
            image2.visibility = View.VISIBLE
            Glide.with(this)
                .load("https://c-app.in/uploads/register/${imageurl.get(0)}")
                .into(image1)
            Glide.with(this)
                .load("https://c-app.in/uploads/register/${imageurl.get(1)}")
                .into(image2)
            if (imageurl.size>2){
                image3.visibility = View.VISIBLE
                Glide.with(this)
                    .load("https://c-app.in/uploads/register/${imageurl.get(2)}")
                    .into(image3)
                if (imageurl.size>3){
                    img4th.visibility = View.VISIBLE
                    Glide.with(this)
                        .load("https://c-app.in/uploads/register/${imageurl.get(3)}")
                        .into(image4)
                    if (imageurl.size>4){
                        imgCountLayout.visibility = View.VISIBLE
                        imgCount.setText("+ ${imageurl.size-3}")
                    }
                }
            }

        }

        editText = findViewById(R.id.editPostText)
        editText.setText(text)

        vid = findViewById(R.id.editpostVideo0)
        submitPost = findViewById(R.id.submitEditPost)
        selectVideo = findViewById(R.id.select_editVideo)
        selectImage = findViewById(R.id.select_editImage)

        selectImage.setOnClickListener {
            getCont.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        selectVideo.setOnClickListener {
            getVideoContent.launch("video/*")
        }

        submitPost.setOnClickListener {
            val comment = editText.text.toString()
            if (comment.length > 0){
                editPost(id,userId,groupid,comment,img)
            }
            else{
                Toast.makeText(this@EditUserPostActivity, "Please give text input!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }
        return "image.jpg"
    }

    fun getImageFileFromUri(context: Context, imageUri: Uri): File? {
        val contentResolver = context.contentResolver

        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val fileName = getFileNameFromUri(context, imageUri)

            if (inputStream != null) {
                val imageFile = File(context.cacheDir, fileName)

                FileOutputStream(imageFile).use { outputStream ->
                    val buffer = ByteArray(4 * 1024) // 4K buffer size
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
                return imageFile
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun upload(){
        val progressDialog = Dialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

        val client = OkHttpClient()
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for (imageUr in uri) {
            val file = getImageFileFromUri(this,imageUr)
            val requestBody = file?.asRequestBody("image/*".toMediaTypeOrNull())
            requestBodyBuilder.addFormDataPart("files[]", file?.name, requestBody!!)
        }
        val requestBody = requestBodyBuilder.build()

        val request = Request.Builder()
            .url("https://c-app.in/api/imageupload2")
            .post(requestBody)
            .build()


        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Toast.makeText(this@EditUserPostActivity, "Failed", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    progressDialog.dismiss()
                    val responseBody = response.body?.string()
                    Log.d("MyApp2", responseBody.toString())
                    var stringRes = ""
                    var i: Int = 0
                    val gson = Gson()
                    val items: Array<String> = gson.fromJson(responseBody, Array<String>::class.java)
                    while(i < items.size){
                        if(i == items.size -1){
                            stringRes = stringRes + "${items[i]}"
                        }else {
                            stringRes = stringRes + "${items[i]},"
                        }
                        i++
                    }

                    Log.d( "resString2", stringRes)
                    img = stringRes
                } else {
                    Toast.makeText(this@EditUserPostActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }


        })
    }

    private fun videoUpload(){
        val progressDialog = Dialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

        val filesDir = applicationContext.filesDir
        val file = File(filesDir, "video.mp4")
        val inputStream = contentResolver.openInputStream(uriVideo)
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)

        val responseBody = file.asRequestBody("video/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, responseBody)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.uploadVideo(part)
            video = response.toString()
            Log.d("videoff", video)
            progressDialog.dismiss()
        }
    }


    private fun editPost(id:String, userId: String,groupid:String, comment:String, image:String){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val request = updateNews(id,userId,groupid,comment,image,video)
        val response = apiService.updateNews(request)

        response.enqueue(object : Callback<LikeResponse> {
            override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                finish()
                Toast.makeText(this@EditUserPostActivity, "Successfully Edited", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                Toast.makeText(this@EditUserPostActivity, "Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


}