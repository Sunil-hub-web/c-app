package com.net.capp

import SharedPref
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar

class LocationScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_screen)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_location_screen)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(null)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        SharedPref.initialize(this)

        val selectManual = findViewById<Button>(R.id.manual_location)
        selectManual.setOnClickListener{
            val intent = Intent(this, SelectLocation::class.java)
            startActivity(intent)
        }
    }

}