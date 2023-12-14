package com.example.ics26011_finalsapp

import android.content.Context

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences("LoggedInUser", Context.MODE_PRIVATE)

    fun saveLoggedInUserId(userId: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong("userId", userId)
        editor.apply()
    }

    fun getLoggedInUserId(): Long {
        return sharedPreferences.getLong("userId", -1) // -1 is a default value if the key is not found
    }

    // Clear the logged-in user details
    fun clearLoggedInUser() {
        val editor = sharedPreferences.edit()
        editor.remove("userId")
        editor.remove("userName")
        editor.apply()
    }

    // Clear all SharedPreferences data (use with caution)
    fun clearAllSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}