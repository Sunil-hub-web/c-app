package com.net.capp

import android.app.Application
import androidx.emoji2.bundled.BundledEmojiCompatConfig

class capp : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = BundledEmojiCompatConfig(applicationContext)
        EmojiCompat.init(config)
    }
}