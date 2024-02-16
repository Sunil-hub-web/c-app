package com.net.capp

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostSearchFragment : Fragment() {
    lateinit var SearchPost: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_search, container, false)
        val searchQuery = arguments?.getString("searchQuery")
        SearchPost = view.findViewById(R.id.searchNewsText)

        if (!searchQuery.isNullOrEmpty()) {
            loadsearch(view,searchQuery.toString())
        }
        return view

    }

    fun loadsearch(view:View, searchedItem:String){

        val progressDialog = Dialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val searched = searchedItem(searchedItem)
        val response = apiService.getsearchData(searched)

        response.enqueue(object : Callback<data_class_search> {
            override fun onResponse(call: Call<data_class_search>, response: Response<data_class_search>) {
                if (response.isSuccessful) {
                    val bodyR = response.body()?.data!!
                    val recyclerView = view.findViewById<RecyclerView>(R.id.postRecycler)
                    recyclerView.visibility = View.VISIBLE
                    Log.d("xxy",bodyR.toString())
                    val adapter = SearchPostAdapter(bodyR, requireContext())
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    progressDialog.dismiss()
                }
                else {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "No Result found", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<data_class_search>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Request Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

}