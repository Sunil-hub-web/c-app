package com.net.capp

import SharedPref
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.security.auth.callback.Callback


class MainActivity : AppCompatActivity() {
    //Fragments
    lateinit var addFloat: FloatingActionButton
    lateinit var bottomNavigationView: BottomNavigationView
    val TAG = "ContentValues"
    var hasNotificationPermissionGranted = false

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                            showNotificationPermissionRationale()
                        } else {
                            showSettingDialog()
                        }
                    }
                }
            } else {
                //Toast.makeText(applicationContext, "notification permission granted", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale() {

        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SharedPref.initialize(this)
        SharedPref.setLogin()

        //float action button
        addFloat = findViewById(R.id.fab_add)
        addFloat.setOnClickListener {
            val bottomSheetDialog = BottomDialogFragment()
            bottomSheetDialog.show(supportFragmentManager, null)
//            showOptionsMenu()
        }

        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }

//        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
//            showNotification()
//        }

        getFcmToken()

        //when click bottom navigation view
        var NavController = findNavController(R.id.container_frame)
        var bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(NavController)

//        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
//        val loginOpened = prefs.getBoolean("loginOpened", false)
//        val otpOpened = prefs.getBoolean("OTPOpened", false)
//        val introOpened = prefs.getBoolean("IntroOpened", false)
//        val languageOpened = prefs.getBoolean("languageOpened", false)
//        val termOpened = prefs.getBoolean("TermOpened", false)
//        if (!loginOpened && !otpOpened && !introOpened && !languageOpened && !termOpened) {
//            val intent = Intent(this@MainActivity, login_activity::class.java)
//            startActivity(intent)
//        }
    }

    fun getFcmToken() {

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                // this fail
                if (!task.isSuccessful) {
                    Log.d(
                        TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@addOnCompleteListener
                }
                //this token
                val token = task.result
                Log.d(TAG, token)
                //to showing
                // binding!!.token.setText(token)
                //Toast.makeText(this@MainActivity, "get a token", Toast.LENGTH_SHORT).show()

                SharedPref.initialize(this)
                val myUserId = SharedPref.getUserId().toString()

                var androidID = Settings.Secure.getString(this@MainActivity.getContentResolver(), Settings.Secure.ANDROID_ID);

                Log.d("useriddata12",myUserId)
                Log.d("useriddata14",androidID)

                //pushfcmdata(myUserId, androidID, token)
            }
    }

    private fun showNotification() {

        val channelId = "12345"
        val description = "Test Notification"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.lightColor = Color.BLUE

            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

        }

        val  builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Hello World")
            .setContentText("Test Notification")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources, R.drawable
                        .ic_launcher_background
                )
            )
        notificationManager.notify(12345, builder.build())
    }

    fun pushfcmdata(userid:String, deviceId:String, fcmToken:String){

        val progressDialog = Dialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_dialog)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://c-app.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val response = apiService.deviceiddata(userid, deviceId, fcmToken)

        response.enqueue(object: retrofit2.Callback<isSuccess> {

            override fun onResponse(call: Call<isSuccess>, response: Response<isSuccess>) {
                val resp = response.body()?.success.toString()
                progressDialog.dismiss()

                if (resp.equals("true")){

                    Toast.makeText(this@MainActivity, "success", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<isSuccess>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
            }



        })
    }
  }