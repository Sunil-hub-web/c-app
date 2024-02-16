package com.net.capp

import SharedPref
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AccountFragment : Fragment() {
    lateinit var locationTextView: TextView
    lateinit var usernameTextView: TextView
    lateinit var aboutTextView: TextView
    lateinit var userImage: ImageView

    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    lateinit var followers: LinearLayout
    lateinit var followings: LinearLayout
    lateinit var postCount: TextView
    lateinit var followersCount: TextView
    lateinit var followingCount: TextView

    lateinit var imageView: ImageView
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SharedPref.initialize(requireContext())
        val userId = SharedPref.getUserId().toString()
        Log.d("userid", userId)

        val view = inflater.inflate(R.layout.fragment_account, container, false)
        locationTextView = view.findViewById(R.id.location_textview)
        usernameTextView = view.findViewById(R.id.AccounttextView)
        aboutTextView = view.findViewById(R.id.about_textview)
        userImage = view.findViewById(R.id.userImageAccount)

        followers = view.findViewById(R.id.followers)
        followings = view.findViewById(R.id.followings)

        followersCount = view.findViewById(R.id.followersCount)
        followingCount = view.findViewById(R.id.followingCount)
        postCount = view.findViewById(R.id.postCount)

        imageView = view.findViewById(R.id.option_menu_button)
        imageView.setOnClickListener {
            showOptionsMenu(imageView)
        }

        followers.setOnClickListener {
            startActivity(Intent(requireContext(), FollowersActivity::class.java))
        }
        followings.setOnClickListener {
            startActivity(Intent(requireContext(), FollowingActivity::class.java))
        }
        showData(userId)

//        tabLayout = view.findViewById(R.id.tab_layout)
//        tabLayout.addTab(tabLayout.newTab().setText("Posts"))
//        tabLayout.addTab(tabLayout.newTab().setText("Followers"))
//        tabLayout.addTab(tabLayout.newTab().setText("Following"))
//
//        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                val position = tab.position
//                if (position == 0) {
//                }
//                else if (position == 1) {
//                    startActivity(Intent(requireContext(), FollowersActivity::class.java))
//                }
//                else if (position == 2) {
//                    startActivity(Intent(requireContext(), FollowingActivity::class.java))
//                }
//            }
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//            }
//            override fun onTabReselected(tab: TabLayout.Tab) {
//            }
//        })

        // Find the drawer layout in the layout file
        drawerLayout = view.findViewById(R.id.drawer_layout)
        val openDrawerButton: ImageView = view.findViewById(R.id.openDrawer)
        openDrawerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        // Set up the navigation view
        navigationView = view.findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_settings ->
                    startActivity(Intent(requireContext(), SettingActivity::class.java))

                R.id.nav_terms_conditions ->
                    startActivity(Intent(requireContext(), TermsAndConditionsNav::class.java))

                R.id.nav_privacy_policy ->
                    startActivity(Intent(requireContext(), PrivacyPolicy::class.java))
                R.id.nav_logout ->{
                    SharedPref.logoutUser(requireContext())
                    Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
                }
            }

            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }

        // posts
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
        val call = apiService.getUserData(userId)

        call.enqueue(object : Callback<getUserNews> {
            override fun onResponse(
                call: Call<getUserNews>,
                response: Response<getUserNews>
            ) {
                if (response.isSuccessful){
                    val success = response.body()?.success!!
                    if (success){
                        val body = response.body()?.data!!
                        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerAccount)
                        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
                        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.line)!!)
                        recyclerView.addItemDecoration(dividerItemDecoration)

                        val adapter = AccountAdapter(body, requireContext())
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        progressDialog.dismiss()
                    }
                    else{
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Request Failed", Toast.LENGTH_SHORT).show()
                    }

                }
                else{
                    Toast.makeText(requireContext(), "No Posts", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }

            }
            override fun onFailure(call: Call<getUserNews>, t: Throwable) {
                Toast.makeText(requireContext(), "No Post", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        })
        return view
    }

    private fun showOptionsMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.option_menu_item, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.option_edit -> {
                    val intent = Intent(getActivity(), EditAccountActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.option_delete -> {
                    Toast.makeText(getActivity(), "Delete is clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun showData(userId: String){
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
                val data = response.body()?.data
                val userData = response.body()?.data?.user
                Glide.with(requireContext())
                    .load("${userData?.image}")
                    .into(userImage)

                userImage.setOnClickListener {
                    val intent = Intent(requireContext(), ImageViewer::class.java)
                    intent.putExtra("image",userData?.image)
                    startActivity(intent)
                }

                data?.let {
                    postCount.setText(data.postcount.toString())
                    followingCount.setText(data.followedCount.toString())
                    followersCount.setText(data.followersCount.toString())
                    usernameTextView.setText(data.user.username)
                    locationTextView.setText(data.user.city+", "+data.user.state)
                    aboutTextView.setText(data.user.about)
                }
            }
            override fun onFailure(call: Call<UserData>, t: Throwable) {
            }
        })
    }


}