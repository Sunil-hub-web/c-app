package com.net.capp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SelectLocation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val progressDialog = Dialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

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
                        progressDialog.dismiss()
                        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                        recyclerView.layoutManager = LinearLayoutManager(this@SelectLocation)

                        val dividerItemDecoration = DividerItemDecoration(this@SelectLocation, LinearLayoutManager.VERTICAL)
                        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this@SelectLocation, R.drawable.line)!!)
                        recyclerView.addItemDecoration(dividerItemDecoration)

                        val adapter = StateAdapter(stateList,this@SelectLocation)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@SelectLocation, "Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StateResponse>, t: Throwable) {

            }
        })
    }


}