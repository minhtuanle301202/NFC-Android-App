package com.example.todoapp

import android.content.Context
import android.content.SharedPreferences

// SharedPreferencesManager.kt
class SharedPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()  // Khai báo editor


    companion object {
        private const val PREF_NAME = "LoginPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_LOGIN_TIME = "loginTime"
        private const val KEY_TOKEN = "token"
        private const val SESSION_TIMEOUT = 60*60 * 1000 // 24 giờ
        private const val SESSION_EMAIL = "email"
    }


    fun isLoggedIn(): Boolean {
        // Bước 1: Kiểm tra trạng thái đăng nhập
        if (!prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            return false
        }

        // Bước 2: Lấy thời gian đăng nhập
        val loginTime = prefs.getLong(KEY_LOGIN_TIME, 0)
        val currentTime = System.currentTimeMillis()

        // Bước 3: Kiểm tra timeout
        return if (currentTime - loginTime > SESSION_TIMEOUT) {
            // Session đã hết hạn
            clearSession()
            false
        } else {
            // Session còn hạn
            true
        }
    }

    // Lưu thời gian đăng nhập khi user login thành công
    fun saveLoginSession(token: String,email: String) {
        editor.apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_TOKEN, token)
            putString(SESSION_EMAIL,email)
            putLong(KEY_LOGIN_TIME, System.currentTimeMillis()) // Lưu thời điểm đăng nhập
            apply()
        }
    }

    fun getEmail():String {
        val email = prefs.getString("email","") ?: ""
        return email
    }
    // Xóa session khi logout hoặc hết hạn
    fun clearSession() {
        editor.apply {
            clear()
            apply()
        }
    }
}