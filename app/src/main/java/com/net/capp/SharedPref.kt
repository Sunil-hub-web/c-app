//package com.example.themitra

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.net.capp.LanguageSelectionActivity

class SharedPref {
    companion object {
        private const val PREF_NAME = "sharedcheckLogin"
        private const val IS_LOGIN = "islogin"
        private const val STATE_NAME = "state"
        private const val CITY_NAME = "city"
        private const val CITY_ID = "Id"
        private const val IS_LanguageOpened = "isLanguageOpened"
        private const val Is_IntroOpened = "IsIntroOpened"
        private const val Is_LocationOpened = "IsLocationOpened"
        private const val LANGUAGE = "language"
        private const val USER_ID = "id"
        private const val USERPHONENUMBER = "userPhoneNumber"

        private lateinit var sharedPreferences: SharedPreferences

        fun initialize(context: Context) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        fun isLogin(): Boolean?{
            return sharedPreferences.getBoolean(IS_LOGIN, false)
        }
        fun setLogin() {
            val editor = sharedPreferences.edit()
            editor.putBoolean(IS_LOGIN, true)
            editor.apply()
        }

        fun isLanguageOpened(): Boolean?{
            return sharedPreferences.getBoolean(IS_LanguageOpened, false)
        }

        fun setLanguageOpened() {
            val editor = sharedPreferences.edit()
            editor.putBoolean(Is_IntroOpened, true)
            editor.apply()
        }

        fun isIntroOpened(): Boolean?{
            return sharedPreferences.getBoolean(Is_IntroOpened, false)
        }

        fun setIntroOpened() {
            val editor = sharedPreferences.edit()
            editor.putBoolean(Is_IntroOpened, true)
            editor.apply()
        }

        fun isLocationOpened(): Boolean?{
            return sharedPreferences.getBoolean(Is_LocationOpened, false)
        }

        fun setLocationOpened() {
            val editor = sharedPreferences.edit()
            editor.putBoolean(Is_LocationOpened, true)
            editor.apply()
        }

        fun setCityId(name: String) {
            val editor = sharedPreferences.edit()
            editor.putString(CITY_ID, name)
            editor.apply()
        }

        fun getCityId(): String? {
            return sharedPreferences.getString(CITY_ID, null)
        }

        fun setStateName(name: String) {
            val editor = sharedPreferences.edit()
            editor.putString(STATE_NAME, name)
            editor.apply()
        }

        fun getStateName(): String? {
            return sharedPreferences.getString(STATE_NAME, null)
        }
        fun setCityName(name: String) {
            val editor = sharedPreferences.edit()
            editor.putString(CITY_NAME, name)
            editor.apply()
        }

        fun getUserId(): String? {
            return sharedPreferences.getString(USER_ID, null)
        }
        fun setUserId(name: String) {
            val editor = sharedPreferences.edit()
            editor.putString(USER_ID, name)
            editor.apply()
        }

        fun getCityName(): String? {
            return sharedPreferences.getString(CITY_NAME, null)
        }

        fun getUserPhonenumber(): String?{
            return sharedPreferences.getString(USERPHONENUMBER, "DEFAULT")
        }

        fun setuserPhonenumber(uPhone: String) {
            val editor = sharedPreferences.edit()
            editor.putString(USERPHONENUMBER, uPhone)
            editor.apply()
        }

        fun language(): String?{
            return sharedPreferences.getString(LANGUAGE, "DEFAULT")
        }

        fun saveLanguage(language: String) {
            val editor = sharedPreferences.edit()
            editor.putString(LANGUAGE, language)
            editor.apply()
        }

        fun logoutUser(context:Context) {
            val editor = sharedPreferences.edit()
            // Clearing all data from Shared Preferences
            editor.putBoolean(IS_LOGIN, false)
            editor.apply()
            editor.clear()
            val i = Intent(context, LanguageSelectionActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }
    }
}
