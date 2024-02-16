package com.net.capp

import SharedPref
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LanguageSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_selection)

        SharedPref.initialize(this)

        val englishButton = findViewById<Button>(R.id.english_button)
        val hindiButton = findViewById<Button>(R.id.hindi_button)

        englishButton.setOnClickListener {
            SharedPref.saveLanguage("en")
            startIntroductionActivity()
        }

        hindiButton.setOnClickListener {
            SharedPref.saveLanguage("hi")
            startIntroductionActivity()
        }
    }

    private fun startIntroductionActivity() {
        val intent = Intent(this@LanguageSelectionActivity, IntroductionActivity::class.java)
        startActivity(intent)
    }
    override fun onStart() {
        super.onStart()
        SharedPref.initialize(this)
        val cond = SharedPref.isLogin()
        if (cond == true){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}