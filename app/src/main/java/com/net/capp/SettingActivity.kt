package com.net.capp

import SharedPref
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import java.util.Locale

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val toolbar = findViewById<Toolbar>(R.id.setting_toolbar)

        SharedPref.initialize(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val english = findViewById<LinearLayout>(R.id.english)
        val hindi = findViewById<LinearLayout>(R.id.hindi)

        english.setOnClickListener {
            SharedPref.saveLanguage("en")
            val locale = Locale("hi")
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.setLocale(locale)
            baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
            Toast.makeText(this, "English is selected", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,MainActivity::class.java))
            finishAffinity()
        }
        hindi.setOnClickListener {
            SharedPref.saveLanguage("hi")
            Toast.makeText(this, "Hindi is selected", Toast.LENGTH_SHORT).show()
            val locale = Locale("hi")
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.setLocale(locale)
            baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)

            finishAffinity()
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}