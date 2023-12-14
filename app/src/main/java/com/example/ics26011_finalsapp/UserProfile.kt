package com.example.ics26011_finalsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class UserProfile : AppCompatActivity() {
    // Use the same key as in SharedPreferencesManager
    private val sharedPreferencesKey = "LoggedInUser"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Retrieve the user ID and user name from SharedPreferences
        val sharedPreferences = getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", -1)
        val userName = sharedPreferences.getString("userName", "DefaultUserName")

        // Display the user name in a TextView
        val usernameTextView = findViewById<TextView>(R.id.textUsername)
        usernameTextView.text = "$userName"

        val navGrades = findViewById<Button>(R.id.navGrades)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        navGrades.setOnClickListener {
            // Finish the current activity before starting Grades activity
            finish()
            val intent = Intent(this, Grades::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            // Clear SharedPreferences
            clearSharedPreferences()

            // Finish the current activity before starting MainActivity
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun clearSharedPreferences() {
        val sharedPreferences = getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}