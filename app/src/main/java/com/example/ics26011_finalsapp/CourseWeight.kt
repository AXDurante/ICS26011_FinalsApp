package com.example.ics26011_finalsapp

import DatabaseHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

class CourseWeight : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_weight)

        // DBHelper
        dbHelper = DatabaseHelper(this)

        // Fetch parent items from the database
        val userId = SharedPreferencesManager(this).getLoggedInUserId()
        val parentList = dbHelper.fetchParentList(userId)

        // Assuming you have a function to get child items for a parent (assessment)
        val getChildItems: (Long) -> List<ChildItem> = { assessmentId ->
            dbHelper.getActivitiesFromDatabase(assessmentId.toInt(), userId)
        }

        // Create an instance of ParentAdapter
        val parentAdapter = ParentAdapter(parentList, userId, getChildItems) { assessmentId, userId ->
            // Call the function to show the add activity dialog
            showAddActivityDialog(assessmentId, userId)
        }

        // Find the RecyclerView in your layout
        val parentRecyclerView: RecyclerView = findViewById(R.id.assessmentRecyclerView)

        // Set up layout manager and adapter for RecyclerView
        parentRecyclerView.layoutManager = LinearLayoutManager(this)
        parentRecyclerView.adapter = parentAdapter

        // Retrieve course details from the intent
        val courseId = intent.getLongExtra("courseId", -1)
        val courseName = intent.getStringExtra("courseName")

        val selectedCourseTextView: TextView = findViewById(R.id.courseTitle)
        val btnBack: ImageButton = findViewById(R.id.btnReturn)
        val btnDelete: ImageButton = findViewById(R.id.deleteCourse)
        val editCourse: ImageButton = findViewById(R.id.editCourse)

        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Return to Grades
        btnBack.setOnClickListener {
            val i = Intent(this, Grades::class.java)
            startActivity(i)
            finish()
        }

        // Set the course name to the TextView
        selectedCourseTextView.text = courseName

        editCourse.setOnClickListener {
            showEditDialog()
        }

        val btnAddAssessment: Button = findViewById(R.id.addAssessment)
        btnAddAssessment.setOnClickListener {
            // Show the dialog to add a new assessment
            showAddAssessmentDialog(courseId)
        }
    }

    // For Editing Course

    private fun showEditDialog() {
        // Retrieve course details from the intent
        val courseId = intent.getLongExtra("courseId", -1)
        val courseName = intent.getStringExtra("courseName")

        val dialogView = layoutInflater.inflate(R.layout.dialog_update_course, null)
        val courseNameEditText: TextInputEditText = dialogView.findViewById(R.id.enterCourseName)
        val courseDescEditText: TextInputEditText = dialogView.findViewById(R.id.enterDescription)
        val updateButton: Button = dialogView.findViewById(R.id.updateCourse)

        // Set the initial values in the EditText fields
        courseNameEditText.setText(courseName)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()

        updateButton.setOnClickListener {
            val updatedCourseName = courseNameEditText.text.toString()
            val updatedCourseDesc = courseDescEditText.text.toString()

            if (updatedCourseName.isNotEmpty() && updatedCourseDesc.isNotEmpty()) {
                // Update the database with the new information
                updateCourseInDatabase(courseId, updatedCourseName, updatedCourseDesc)

                // Update the course name in the current page
                val selectedCourseTextView: TextView = findViewById(R.id.courseTitle)
                selectedCourseTextView.text = updatedCourseName

                // Close the dialog
                alertDialog.dismiss()
            } else {
                // Handle empty inputs if needed
                Toast.makeText(this, "Please enter both course name and description", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCourseInDatabase(courseId: Long, updatedCourseName: String, updatedCourseDesc: String) {
        // Use your DatabaseHelper to update the course information in the database
        val dbHelper = DatabaseHelper(this)
        dbHelper.updateCourseInDatabase(courseId, updatedCourseName, updatedCourseDesc)
        finish()
    }


    // For Delete Course
    private fun showDeleteConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_delete, null)
        val btnConfirmDelete: Button = dialogView.findViewById(R.id.btnConfirmDelete)
        val btnCancelDelete: Button = dialogView.findViewById(R.id.btnCancelDelete)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()

        btnConfirmDelete.setOnClickListener {
            // Retrieve course details from the intent
            val courseId = intent.getLongExtra("courseId", -1)

            // Delete the course from the database
            deleteCourseFromDatabase(courseId)

            // Set the result as deleted
            setResult(RESULT_OK)

            // Close the dialog
            alertDialog.dismiss()

            // Finish the activity immediately after deletion
            finish()
        }

        btnCancelDelete.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun deleteCourseFromDatabase(courseId: Long) {
        val userId = SharedPreferencesManager(this).getLoggedInUserId()

        // Use your DatabaseHelper to delete the course information and associated assessments and activities
        val dbHelper = DatabaseHelper(this)
        dbHelper.deleteCourseFromDatabase(courseId, userId)
    }

    // For Adding Assessment
    private fun showAddAssessmentDialog(courseId: Long) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_assessment, null)
        val assessmentNameEditText: TextInputEditText = dialogView.findViewById(R.id.enterAssessmentName)
        val gradeWeightEditText: TextInputEditText = dialogView.findViewById(R.id.enterWeight)
        val saveButton: Button = dialogView.findViewById(R.id.createAssessment)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()

        saveButton.setOnClickListener {
            val assessmentName = assessmentNameEditText.text.toString()
            val gradeWeightText = gradeWeightEditText.text.toString()

            // Check if the inputs are not empty
            if (assessmentName.isNotEmpty() && gradeWeightText.isNotEmpty()) {
                val gradeWeight = gradeWeightText.toDouble() // Convert to the appropriate type

                // Get the currently logged in user's ID
                val userId = SharedPreferencesManager(this).getLoggedInUserId()

                // Call a function to save the assessment to the database
                saveAssessmentToDatabase(userId, courseId, assessmentName, gradeWeight)

                alertDialog.dismiss()

                recreate()
            } else {
                // Handle empty inputs if needed
            }
        }
    }

    // Function to save a new assessment
    private fun saveAssessmentToDatabase(userId: Long, courseId: Long, assessmentName: String, gradeWeight: Double) {
        // Use DatabaseHelper to save the assessment to the database
        val dbHelper = DatabaseHelper(this)
        dbHelper.saveAssessmentToDatabase(userId, courseId, assessmentName, gradeWeight)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Close the database when the activity is destroyed
        dbHelper.close()
    }

    // Function to show the dialog for adding a new activity
    private fun showAddActivityDialog(userId: Long, courseId: Long) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_activity, null)
        val enterActivity: TextInputEditText = dialogView.findViewById(R.id.enterActivity)
        val enterUserScore: TextInputEditText = dialogView.findViewById(R.id.enterUserScore)
        val enterMaxScore: TextInputEditText = dialogView.findViewById(R.id.enterMaxScore)
        val createActivityButton: Button = dialogView.findViewById(R.id.createActivity)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()

        createActivityButton.setOnClickListener {
            val activityName = enterActivity.text.toString()
            val userScoreText = enterUserScore.text.toString()
            val maxScoreText = enterMaxScore.text.toString()

            if (activityName.isNotEmpty() && userScoreText.isNotEmpty() && maxScoreText.isNotEmpty()) {
                val userScore = userScoreText.toDouble()
                val maxScore = maxScoreText.toDouble()

                // Save the activity to the database
                saveActivityToDatabase(userId, courseId, activityName, userScore, maxScore)

                // Dismiss the dialog
                alertDialog.dismiss()

                // Refresh the UI (recreate the activity)
                recreate()
            } else {
                // Handle empty inputs if needed
                Toast.makeText(
                    this,
                    "Please enter all fields for the activity",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Function to save a new activity
    private fun saveActivityToDatabase(
        userId: Long,
        courseId: Long,
        activityName: String,
        userScore: Double,
        maxScore: Double
    ) {
        dbHelper.saveActivityToDatabase(userId, courseId, activityName, userScore, maxScore)
    }
}