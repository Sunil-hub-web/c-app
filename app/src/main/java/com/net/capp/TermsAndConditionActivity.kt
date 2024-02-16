package com.net.capp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class TermsAndConditionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_condition)

        val submit = findViewById<Button>(R.id.submit_button)
        val checkBox = findViewById<CheckBox>(R.id.checkbox_agree)

        val toolbar = findViewById<Toolbar>(R.id.term_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        submit.setOnClickListener{
            if (checkBox.isChecked()) {
                startActivity(Intent(this@TermsAndConditionActivity, login_activity::class.java))
            } else {
                Toast.makeText(
                    this@TermsAndConditionActivity,
                    "Please agree to the terms and conditions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}