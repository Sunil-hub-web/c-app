package com.net.capp
import SharedPref
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class otp_acivity : AppCompatActivity() {
    private lateinit var otpEditText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_acivity)
        val toolbar = findViewById<Toolbar>(R.id.otp_toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(null)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        otpEditText = findViewById(R.id.otp_edit_text)
        val phone = intent.getStringExtra("phone").toString()

        val submitButton = findViewById<AppCompatButton>(R.id.submit_button)
        submitButton.setOnClickListener {
            val otp = otpEditText.text.toString()
            verify_otp(phone,otp)
        }
    }

    private fun verify_otp(phone:String, otp: String) {

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

        val Request = data_class_verifyOtp(phone,otp)
        val response = apiService.verifyOtp(Request)

        response.enqueue(object: Callback<OtpResponce>{
            override fun onResponse(call: Call<OtpResponce>, response: Response<OtpResponce>) {
                val resp = response.body()?.success.toString()
                progressDialog.dismiss()
                if (resp.equals("true")){
                    SharedPref.initialize(this@otp_acivity)
                    SharedPref.setUserId(response.body()?.user?.id.toString())
                    val intent = Intent(this@otp_acivity, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                else{
                    Toast.makeText(this@otp_acivity, "Enter Valid OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OtpResponce>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@otp_acivity, "Failed", Toast.LENGTH_SHORT).show()
            }

        })
    }

}