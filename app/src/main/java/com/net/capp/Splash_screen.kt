package com.net.capp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity


class Splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = PreferenceManager.getDefaultSharedPreferences(this@Splash_screen)
            val isUserLoggedIn = prefs.getBoolean("isUserLoggedIn", false)

            if (isUserLoggedIn) {
                Intent(this@Splash_screen, MainActivity::class.java)
            } else {
                Intent(this@Splash_screen, LanguageSelectionActivity::class.java)
            }
            val intent = Intent(this, LanguageSelectionActivity::class.java)
            startActivity(intent)
            finish()
        },1000)
    }
}