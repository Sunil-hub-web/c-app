package com.net.capp

import SharedPref
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class HomeFragment : Fragment() {

    lateinit var adapter: HomeAdapter
    lateinit var refresh: SwipeRefreshLayout
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        SharedPref.initialize(requireContext())
        val userId = SharedPref.getUserId().toString()

        val language = SharedPref.language()!!

        load(language)


        val view = inflater.inflate(R.layout.fragment_home, container, false)

//        val toolbar = view.findViewById<Toolbar>(R.id.home_toolbar)
        val search = view.findViewById<ImageView>(R.id.search_home)
        val invite = view.findViewById<TextView>(R.id.invite)
        refresh = view.findViewById(R.id.swipeRefresh)
        recyclerView = view.findViewById(R.id.recyclerHome)


        refresh.setOnRefreshListener {
            fetchData(userId)
        }


        search.setOnClickListener {
            context?.startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        invite.setOnClickListener {
            val invite = "Hey there! Check Out this Awesome Social Media App \nhttps://play.google.com/store/apps/details?id=com.net.capp&pli=1"
            val intent = Intent()
            intent.setAction(Intent.ACTION_SEND_MULTIPLE)
            intent.putExtra(Intent.EXTRA_TEXT,invite)
            intent.setType("text/plane")
            context?.startActivity(Intent.createChooser(intent, "Invite"))
        }

        fetchData(userId)

        return view
    }

    fun fetchData(userId: String){
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
        val call = apiService.getHomeData("")

        call.enqueue(object : Callback<data_class_home> {
            override fun onResponse(
                call: Call<data_class_home>,
                response: Response<data_class_home>
            ) {
                if (response.isSuccessful){
                    val body = response.body()?.data!!
                    progressDialog.dismiss()
                    adapter = HomeAdapter(body, requireContext())
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    refresh.isRefreshing = false
                }
                else{
                    refresh.isRefreshing = false
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "No Posts", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<data_class_home>, t: Throwable) {
                refresh.isRefreshing = false
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Request Failed", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun load(language:String){
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.setLocale(locale)
        requireContext().resources.updateConfiguration(configuration,requireContext().resources.displayMetrics)
    }

}