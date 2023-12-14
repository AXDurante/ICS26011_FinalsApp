package com.example.ics26011_finalsapp

import DatabaseHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)

        val loginUsername = findViewById<EditText>(R.id.loginUsername)
        val loginPassword = findViewById<EditText>(R.id.loginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val navSignup = findViewById<Button>(R.id.navSignup)


        btnLogin.setOnClickListener {
            val userName = loginUsername.text.toString()
            val password = loginPassword.text.toString()

            if (databaseHelper.checkUserCredentials(this, userName, password)) {
                showToast("Login successful!")

                // Navigate to MainMenu
                val intent = Intent(this, UserProfile::class.java)
                startActivity(intent)
            } else {
                showToast("Invalid credentials")
            }
        }

        navSignup.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}