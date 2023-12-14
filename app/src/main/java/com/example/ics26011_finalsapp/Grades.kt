package com.example.ics26011_finalsapp

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

class Grades : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<Courses>
    private lateinit var coursesAdapter: CoursesAdapter
    private val COURSE_WEIGHT_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grades)

        val btnReturnProfile = findViewById<ImageButton>(R.id.btnReturnProfile)

        btnReturnProfile.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }

        newRecyclerView = findViewById(R.id.courseRecycler)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf()

        // Initialize the adapter here
        coursesAdapter = CoursesAdapter(newArrayList) { position ->
            onItemClick(position)
        }

        newRecyclerView.adapter = coursesAdapter

        val addCourseButton: Button = findViewById(R.id.addCourse)
        addCourseButton.setOnClickListener {
            showAddCourseDialog()
        }

        getUserData()
    }

    private fun getUserData() {
        val userId = SharedPreferencesManager(this).getLoggedInUserId()

        if (userId != -1L) {
            val userCourses = DatabaseHelper(this).getCoursesFromDatabase(userId)

            newArrayList.clear()
            newArrayList.addAll(userCourses)
            coursesAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
        }
    }

    private fun showAddCourseDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_course, null)
        val courseNameEditText: TextInputEditText = dialogView.findViewById(R.id.enterCourseName)
        val courseDescEditText: TextInputEditText = dialogView.findViewById(R.id.enterDescription)
        val saveButton: MaterialButton = dialogView.findViewById(R.id.saveCourse)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()

        saveButton.setOnClickListener {
            val courseName = courseNameEditText.text.toString()
            val courseDesc = courseDescEditText.text.toString()

            // Check if the inputs are not empty
            if (courseName.isNotEmpty() && courseDesc.isNotEmpty()) {
                val userId = SharedPreferencesManager(this).getLoggedInUserId()
                saveCourseToDatabase(userId, courseName, courseDesc)
                getUserData() // Refresh the RecyclerView after adding a new course
                alertDialog.dismiss()
            } else {
                // Handle empty inputs if needed
            }
        }
    }

    // Function to save a new course
    private fun saveCourseToDatabase(userId: Long, courseName: String, courseDesc: String) {
        val courseId = DatabaseHelper(this).saveCourseToDatabase(userId, courseName, courseDesc)
    }

    fun onItemClick(position: Int) {
        // Get the selected course
        val selectedCourse = newArrayList[position]

        // Assuming courseId and courseName are properties in the Courses class
        val courseId = selectedCourse.course_id
        val courseName = selectedCourse.course_name

        // Redirect to CourseWeight activity and pass the courseId and courseName as extras
        val intent = Intent(this, CourseWeight::class.java)
        intent.putExtra("courseId", courseId)
        intent.putExtra("courseName", courseName)
        startActivityForResult(intent, COURSE_WEIGHT_REQUEST_CODE)
    }

    // Override onActivityResult to handle the result from CourseWeight
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == COURSE_WEIGHT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Handle the result, update the list, etc.
                getUserData()
            }
        }
    }
}
