package com.example.ics26011_finalsapp

import DatabaseHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Register : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this)

        val btnSignup = findViewById<Button>(R.id.btnRegister)
        val navLogin = findViewById<Button>(R.id.navLogin)
        val regUsername = findViewById<EditText>(R.id.regUsername)
        val regPassword = findViewById<EditText>(R.id.regPassword)

        btnSignup.setOnClickListener {
            val username = regUsername.text.toString()
            val password = regPassword.text.toString()

            val userId = databaseHelper.addUser(username, password)

            if (userId > 0) {
                showToast("User registered successfully with ID: $userId")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                showToast("Error registering user")
            }

        }

        navLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}