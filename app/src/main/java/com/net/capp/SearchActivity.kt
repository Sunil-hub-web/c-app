package com.net.capp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

class SearchActivity : AppCompatActivity() {
    lateinit var adapter: ViewpagerAdapter
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout_search)
        tabLayout.addTab(tabLayout.newTab().setText("Users"))
        tabLayout.addTab(tabLayout.newTab().setText("Posts"))
        adapter = ViewpagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab !== null) {
                    viewPager.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
        searchView = findViewById(R.id.searchContent)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("test111", query.toString())

                val currentTab = tabLayout.getTabAt(tabLayout.selectedTabPosition)
                if (currentTab?.text == "Posts") {
                    // Only perform search when the "Posts" tab is selected
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }
    private fun performSearch(query: String) {
        val fragment = PostSearchFragment()
        val bundle = Bundle()
        bundle.putString("searchQuery", query)
        fragment.arguments = bundle

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}