package com.net.capp

import SharedPref
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class login_activity : AppCompatActivity() {
    lateinit var edtName : EditText
    lateinit var edtNum : EditText
    lateinit var submit : AppCompatButton
    val TAG = "ContentValues"
    lateinit var androidID : String
    lateinit var token : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        SharedPref.initialize(this)
        val stateId = SharedPref.getStateName()!!.toString()
        val cityId = SharedPref.getCityName()!!.toString()
        val toolbar = findViewById<Toolbar>(R.id.login_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(null)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        getFcmToken()

        edtName = findViewById(R.id.edtName)
        edtNum = findViewById(R.id.edtPhone)
        submit = findViewById(R.id.sendotpBtn)

        submit.setOnClickListener {
            val name = edtName.text.toString()
            val phone = edtNum.text.toString()
//            SharedPref.setuserName(name)
//            SharedPref.setuserPhonenumber(phone)

            Log.d("name and phone", name+phone)

            if (name.isEmpty()){
                edtName.setError("Name is required")
                edtName.requestFocus()
            }
            if (phone.isEmpty()){
                edtNum.setError("Phone is required")
                edtNum.requestFocus()
            }
            if (phone.length!=10){
                edtNum.setError("Invalid Phone number")
                edtNum.requestFocus()
            }
            else{
                send_otp(phone,name, stateId, cityId)
            }

        }
    }

    private fun send_otp(num:String, name:String, stateId: String, cityId: String){
        val progressDialog = Dialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiInterface::class.java)
        var device = Device(androidID, token)
        val sendOtpRequest = data_class_login(name, num, stateId, cityId, device)
        val response = apiService.sendOtp(sendOtpRequest)

        response.enqueue(object: Callback<OtpResponce>{
            override fun onResponse(call: Call<OtpResponce>, response: Response<OtpResponce>) {
                val abc = response.body().toString()
                Log.d("abcf", abc)
                progressDialog.dismiss()
                val intent = Intent(this@login_activity, otp_acivity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("phone", num)
                startActivity(intent)
            }
            override fun onFailure(call: Call<OtpResponce>, t: Throwable) {
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getFcmToken() {

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                // this fail
                if (!task.isSuccessful) {
                    Log.d(
                        TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@addOnCompleteListener
                }
                //this token
                token = task.result
                Log.d(TAG, token)
                //to showing
                // binding!!.token.setText(token)
                //Toast.makeText(this@MainActivity, "get a token", Toast.LENGTH_SHORT).show()

                SharedPref.initialize(this)
                val myUserId = SharedPref.getUserId().toString()

                androidID = Settings.Secure.getString(this@login_activity.getContentResolver(), Settings.Secure.ANDROID_ID);

                Log.d("useriddata12",myUserId)
                Log.d("useriddata14",androidID)

            }
    }

}