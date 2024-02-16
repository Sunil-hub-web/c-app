package com.net.capp

import SharedPref
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SelectCity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)

        val selectedState = SharedPref.getStateName().toString()

        val stateName = findViewById<TextView>(R.id.stateNameText)
        stateName.text = selectedState

        val back = findViewById<ImageView>(R.id.back1)
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
        val response = apiService.getCity(selectedState)

        response.enqueue(object : Callback<data_class_response_city> {
            override fun onResponse(call: Call<data_class_response_city>, response: Response<data_class_response_city>) {
                if (response.isSuccessful) {
                    val cityList = response.body()?.city
                    cityList?.let {
                        progressDialog.dismiss()
                        val recyclerView = findViewById<RecyclerView>(R.id.cityRecycler)
                        recyclerView.layoutManager = LinearLayoutManager(this@SelectCity)

                        val dividerItemDecoration = DividerItemDecoration(this@SelectCity, LinearLayoutManager.VERTICAL)
                        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this@SelectCity, R.drawable.line)!!)
                        recyclerView.addItemDecoration(dividerItemDecoration)

                        val adapter = CityAdapter(cityList,this@SelectCity)
                        recyclerView.adapter = adapter
                    }
                } else {
                }
            }
            override fun onFailure(call: Call<data_class_response_city>, t: Throwable) {
            }
        })
    }
}