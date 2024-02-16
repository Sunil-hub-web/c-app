package com.net.capp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageViewer : AppCompatActivity() {
    lateinit var Image: ImageView
    lateinit var close: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)
        val image = intent.getStringExtra("image").toString()
        Image = findViewById(R.id.imageView12)
        close = findViewById(R.id.closeViewer)

        close.setOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(image)
            .into(Image)
    }
}