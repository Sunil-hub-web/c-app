package com.net.capp

import SharedPref
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class EditAccountActivity : AppCompatActivity() {
    lateinit var dob : EditText
    lateinit var gender: EditText
    lateinit var username: EditText
    lateinit var phone: EditText
    lateinit var state: EditText
    lateinit var city: EditText
    lateinit var aboutme: EditText
    lateinit var selectedState : String
    lateinit var image : String
    lateinit var selectImg: Button
    lateinit var edit_image: ImageView
    lateinit var uri: Uri

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uri = it!!
        edit_image.setImageURI(it)
        uploadImage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)
        val toolbar = findViewById<Toolbar>(R.id.edit_profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        SharedPref.initialize(this)
        val id = SharedPref.getUserId().toString()

        dob = findViewById(R.id.date_edit_text)
        gender = findViewById(R.id.gender_edit_text)
        state = findViewById(R.id.state_edittext)
        city = findViewById(R.id.city_edittext)
        username = findViewById(R.id.username_edit_text)
        phone = findViewById(R.id.phone_edit_text)
        aboutme = findViewById(R.id.about_edit_text)
        selectImg = findViewById(R.id.edit_img_button)
        edit_image = findViewById(R.id.edit_profile)
        val save = findViewById<TextView>(R.id.save)

        userdetails(id)
        dob.setOnClickListener {
            showDatePickerDialog()
        }
        gender.setOnClickListener {
            showGenderDialog()
        }

        state.setOnClickListener {
            showStateDialog()
        }

        city.setOnClickListener {
            showCityDialog()
        }

        selectImg.setOnClickListener {
            getContent.launch("image/*")
        }

        save.setOnClickListener {
            val username = username.text.toString()
            val phone = phone.text.toString()
            val aboutme = aboutme.text.toString()
            val gender = gender.text.toString()
            val dob = dob.text.toString()
            val state = state.text.toString()
            val city = city.text.toString()
            save(id,username,phone,gender,dob,aboutme,state,city,"")
        }
    }

    fun getFileExtensionFromUri(uri: Uri): String {
        val contentResolver = applicationContext.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
        return fileExtension ?: "jpg"
    }

    private fun uploadImage(){
        val progressDialog = Dialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

        val filesDir = applicationContext.filesDir
        val fileExtension = getFileExtensionFromUri(uri)
        val file = File(filesDir, "image.$fileExtension")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)

        val responseBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("files[]", file.name, responseBody)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.uploadImage(part).get(0)
            image = response
            Log.d("pff", response.toString())
            progressDialog.dismiss()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                dob.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun showGenderDialog() {
        val items = arrayOf("Male", "Female", "Other")
        var selectedItem = 0
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Choose an item")
            .setSingleChoiceItems(items, -1){dialog, which ->
                selectedItem = which
            }
            .setPositiveButton("OK") { dialog, which ->
                gender.setText(items[selectedItem])
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showStateDialog() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getStateData()

        call.enqueue(object : Callback<StateResponse> {
            override fun onResponse(call: Call<StateResponse>, response: Response<StateResponse>) {
                if (response.isSuccessful) {
                    val stateList = response.body()?.state
                    stateList?.let {
                        var items = stateList?.map { it.name } ?: emptyList()
                        var selectedItem = 0
                        val builder = MaterialAlertDialogBuilder(this@EditAccountActivity)
                        builder.setTitle("Choose Your State")
                            .setSingleChoiceItems(items.toTypedArray(), -1){dialog, which ->
                                selectedItem = which
                            }
                            .setPositiveButton("OK") { dialog, which ->
                                state.setText(items[selectedItem])
                                selectedState = items[selectedItem]
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") { dialog, which ->
                                dialog.dismiss()
                            }

                        val dialog = builder.create()
                        dialog.show()
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<StateResponse>, t: Throwable) {
            }
        })
    }

    private fun showCityDialog() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val response = apiService.getCity(selectedState)

        response.enqueue(object : Callback<data_class_response_city> {
            override fun onResponse(call: Call<data_class_response_city>, response: Response<data_class_response_city>) {
                if (response.isSuccessful) {
                    val cityList = response.body()?.city
                    cityList?.let {
                        var items = cityList?.map { it.name } ?: emptyList()
                        var selectedItem = 0
                        val builder = MaterialAlertDialogBuilder(this@EditAccountActivity)
                        builder.setTitle("Choose your city")
                            .setSingleChoiceItems(items.toTypedArray(), -1){dialog, which ->
                                selectedItem = which
                            }
                            .setPositiveButton("OK") { dialog, which ->
                                city.setText(items[selectedItem])
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") { dialog, which ->
                                dialog.dismiss()
                            }

                        val dialog = builder.create()
                        dialog.show()
                    }
                } else {
                }
            }
            override fun onFailure(call: Call<data_class_response_city>, t: Throwable) {
            }
        })
    }


    private fun save(id:String, username:String,phone:String,gender:String,dob:String,aboutme:String,state:String,city:String,police:String){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        Log.d("image2",image)
        val response = apiService.insertProfile(profileInsert(id,username,phone,gender,dob,aboutme,state,city,"",image))

        response.enqueue(object : Callback<profileInsert> {
            override fun onResponse(call: Call<profileInsert>, response: Response<profileInsert>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditAccountActivity, "Successfully Changed", Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(Intent(this@EditAccountActivity,MainActivity::class.java))
                } else {
                }
            }
            override fun onFailure(call: Call<profileInsert>, t: Throwable) {
            }
        })
    }

    private fun userdetails(userId: String){
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
                val data = response.body()?.data!!
                Glide.with(this@EditAccountActivity)
                    .load("${data.user.image}")
                    .into(edit_image)
                username.setText(data.user.username)
                phone.setText(data.user.phone)
                dob.setText(data.user.dob)
                if (data.user.image!=null){
                    image = data.user.image.takeLast(14)
                }
//                    Log.d("image",image)
                gender.setText(data.user.gender)
                aboutme.setText(data.user.about)
                state.setText(data.user.state)
                city.setText(data.user.city)
                selectedState = data.user.state
            }
            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Toast.makeText(this@EditAccountActivity, "User details not fetched", Toast.LENGTH_SHORT).show()
            }
        })
    }
}