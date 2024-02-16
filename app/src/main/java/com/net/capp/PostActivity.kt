 package com.net.capp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri
import okhttp3.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import java.util.concurrent.TimeUnit

class PostActivity : AppCompatActivity() {
    lateinit var removeImage0 : ImageView

    lateinit var img0 : ImageView
    lateinit var img : ImageView
    lateinit var img2 : ImageView
    lateinit var img3 : ImageView
    lateinit var img4 : ImageView
    lateinit var img5 : ImageView
    lateinit var img6 : ImageView

    lateinit var showimageRecycler : RecyclerView
    lateinit var lin_showimage : LinearLayout
    lateinit var adapter : ImageAdapter


    var player : ExoPlayer? = null

    lateinit var vid: PlayerView

    var image: String = ""
    lateinit var submitPost : Button
    lateinit var edittextPost: EditText
    lateinit var selectImage : LinearLayout
    lateinit var selectVideo : LinearLayout
    lateinit var uri: ArrayList<Uri>
    lateinit var uri1: ArrayList<Uri>
    lateinit var uriVideo: Uri
    lateinit var userId: String
    lateinit var districtId: String
    var video: String = ""

    val getContent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        uri = ArrayList()
        for (item in it){
            uri.add(item)
        }

        lin_showimage.visibility = View.VISIBLE

        showimageRecycler.layoutManager = GridLayoutManager(this, 3)
        adapter = ImageAdapter(uri,this)
        showimageRecycler.hasFixedSize()
        showimageRecycler.adapter = adapter

        removeImage0.setOnClickListener {
            try {
                Log.d("exception1", uri.get(0).toString())
                uri.drop(1)
                val arraytwo:ArrayList<Uri> = uri
                for (item in arraytwo){
                    Log.d("exception2", item.toString())
                }
            }
            catch (e:Exception){
                Log.d("exception", e.toString())
            }

        }

//        if (uri.size>0){
//            img0.setImageURI(uri.get(0))
//            if (uri.size>1){
//                img0.visibility = View.GONE
//                img.setImageURI(uri.get(0))
//                img2.setImageURI(uri.get(1))
//                if (uri.size>2){
//                    img3.visibility = View.VISIBLE
//                    img3.setImageURI(uri.get(2))
//                    if (uri.size>3){
//                        img4.setImageURI(uri.get(3))
//                        if (uri.size>4){
//                            img5.setImageURI(uri.get(4))
//                            if (uri.size>5){
//                                img6.setImageURI(uri.get(5))
//                            }
//                        }
//                    }
//                }
//            }
//           // uploadImages()
//        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    val getVideoContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uriVideo = it!!
        vid.visibility = View.VISIBLE

        player = ExoPlayer.Builder(this@PostActivity).build()
        vid.player = player
        val mediaItem = MediaItem.Builder()
            .setUri(it)
            .setMimeType(MimeTypes.APPLICATION_MP4)
            .build()

        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(this@PostActivity)
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
        setContentView(R.layout.activity_post)

        val toolbar = findViewById<Toolbar>(R.id.post_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        SharedPref.initialize(this@PostActivity)
        userId = SharedPref.getUserId().toString()
        districtId = SharedPref.getCityId().toString()

        removeImage0 = findViewById(R.id.removeImage)

        img0 = findViewById(R.id.postImage0)
        showimageRecycler = findViewById(R.id.showimageRecycler)
        lin_showimage = findViewById(R.id.lin_showimage)


        vid = findViewById(R.id.postVideo0)
        submitPost = findViewById(R.id.submitPost)
        selectVideo = findViewById(R.id.select_video)
        selectImage = findViewById(R.id.select_image)
        edittextPost = findViewById(R.id.edittextPost)


        selectImage.setOnClickListener {
            getContent.launch("image/*")
        }
        selectVideo.setOnClickListener {
            getVideoContent.launch("video/*")
        }

        submitPost.setOnClickListener {

            val comment = edittextPost.text.toString()

            if (comment.length > 0){

                uri1 = adapter.getImageArray()

                Log.d("adapterimagearray",uri1.toString())

                uploadImages(uri1)

            }else{

                Toast.makeText(this@PostActivity, "Please give text input!", Toast.LENGTH_SHORT).show()
            }

            //post(userId,districtId)
            //finish()
        }

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

        val okHttpClient = OkHttpClient.Builder()
            .writeTimeout(90, TimeUnit.SECONDS) // Set the write timeout to 60 seconds
            .readTimeout(90, TimeUnit.SECONDS)  // Set the read timeout to 60 seconds
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .client(okHttpClient)
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
                    val buffer = ByteArray(4 * 1024)
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

    private fun uploadImages(uri1: ArrayList<Uri>) {

        val progressDialog = Dialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

        val client = OkHttpClient()
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        var hasParts = false
        for (imageUr in uri1) {
            Log.d("pima1", imageUr.toString())
            val file = getImageFileFromUri(this,imageUr)
            Log.d("test2", file.toString())

            if (file != null) {
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                requestBodyBuilder.addFormDataPart("files[]", file.name, requestBody)
                hasParts = true
            }
        }

        if (hasParts) {
            val requestBody = requestBodyBuilder.build()
            val request = Request.Builder()
                .url("https://c-app.in/api/imageupload2")
                .post(requestBodyBuilder.build())
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    progressDialog.dismiss()
                    Toast.makeText(this@PostActivity, e.toString(), Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        progressDialog.dismiss()
                        Log.d("MyApp", responseBody.toString())
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
                        Log.d( "resString1", stringRes)
                        image = stringRes

                        post(userId,districtId)


                        try {

                        }
                        catch (e:Exception){
                            Log.d("exception12", e.toString())
                        }
                    } else {
                        Log.e("MyApp", "Request failed with code: ${response.code}")
                    }
                }
            })
        }
        else{
            progressDialog.dismiss()
            Log.d("ex", hasParts.toString())
        }
    }

    private fun post(userId: String, districtId:String){

//        val progressDialog = Dialog(this)
//        progressDialog.setCancelable(false)
//        progressDialog.setCanceledOnTouchOutside(false)
//        progressDialog.show()
//        progressDialog.setContentView(R.layout.progress_dialog)


        try {

            val groupid = intent.getStringExtra("groupid").toString()
            val comment = edittextPost.text.toString()

            if (comment.length > 0){

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://c-app.in/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)
                Log.d("exception22", image.toString())
                Log.d("exception23", userId.toString())
                Log.d("exception24", groupid.toString())
                Log.d("exception25", districtId.toString())
                Log.d("exception26", video.toString())

                //Toast.makeText(this@PostActivity, "Posted", Toast.LENGTH_SHORT).show()
                val request = postNews(image,userId,groupid,comment,districtId,video)

                val response = apiService.postNews(request)

                response.enqueue(object : Callback<isSuccess> {
                    override fun onResponse(call: Call<isSuccess>, response: Response<isSuccess>) {
                        val body = response.body()?.success
                        Log.d("exception27", "xyz")
                       // progressDialog.dismiss()

                        Toast.makeText(this@PostActivity, body.toString(), Toast.LENGTH_SHORT).show()

                        finish()

                    }
                    override fun onFailure(call: Call<isSuccess>, t: Throwable) {
                        Log.d("exception28", "amit")
                        //progressDialog.dismiss()
                        Toast.makeText(this@PostActivity, t.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
            } else{

                //progressDialog.dismiss()
                Toast.makeText(this@PostActivity, "Please give text input!", Toast.LENGTH_SHORT).show()
            }

        }
        catch (e:Exception){
            Log.d("exception19", e.toString())
        }

    }

}