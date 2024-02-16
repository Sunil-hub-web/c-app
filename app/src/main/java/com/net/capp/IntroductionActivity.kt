package com.net.capp

import SharedPref
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class IntroductionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        SharedPref.initialize(this)
        val language = SharedPref.language()!!

        load(language)

        val buttonNext = findViewById<FloatingActionButton>(R.id.intro_button_add)
        buttonNext.setOnClickListener {
            startActivity(Intent(this@IntroductionActivity, LocationScreen::class.java))
            finish()
            SharedPref.setIntroOpened()
        }

    }
    fun load(language:String){
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.setLocale(locale)
        this.resources.updateConfiguration(configuration,this.resources.displayMetrics)
    }
}