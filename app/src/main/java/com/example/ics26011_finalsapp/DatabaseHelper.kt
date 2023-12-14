import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ics26011_finalsapp.ChildItem
import com.example.ics26011_finalsapp.Courses
import com.example.ics26011_finalsapp.ParentItem

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "GradeIT"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_USER_NAME = "user_name"
        private const val COLUMN_USER_PASSWORD = "user_password"

        // New tables
        private const val TABLE_COURSES = "courses"
        private const val COLUMN_COURSE_ID = "course_id"
        private const val COLUMN_COURSE_NAME = "course_name"
        private const val COLUMN_COURSE_DESC = "course_desc"
        private const val COLUMN_COURSE_USER_ID = "user_id"

        private const val TABLE_ASSESSMENTS = "assessments"
        private const val COLUMN_ASSESSMENT_ID = "assessment_id"
        private const val COLUMN_ASSESSMENT_NAME = "assessment_name"
        private const val COLUMN_ASSESSMENT_WEIGHT = "grade_weight"
        private const val COLUMN_ASSESSMENT_COURSE_ID = "course_id"
        private const val COLUMN_ASSESSMENT_USER_ID = "user_id"

        private const val TABLE_ACTIVITIES = "activities"
        private const val COLUMN_ACTIVITY_ID = "activity_id"
        private const val COLUMN_ACTIVITY_NAME = "activity_name"
        private const val COLUMN_ACTIVITY_MAX_SCORE = "max_score"
        private const val COLUMN_ACTIVITY_USER_SCORE = "user_score"
        private const val COLUMN_ACTIVITY_ASSESSMENT_ID = "assessment_id"
        private const val COLUMN_ACTIVITY_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = (
                "CREATE TABLE $TABLE_USERS ($COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "$COLUMN_USER_NAME TEXT, $COLUMN_USER_PASSWORD TEXT)")
        db?.execSQL(createUserTable)

        // Creating new tables
        val createCoursesTable = (
                "CREATE TABLE $TABLE_COURSES ($COLUMN_COURSE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "$COLUMN_COURSE_NAME TEXT, $COLUMN_COURSE_DESC TEXT, $COLUMN_COURSE_USER_ID INTEGER, " +
                        "FOREIGN KEY($COLUMN_COURSE_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db?.execSQL(createCoursesTable)

        val createAssessmentsTable = (
                "CREATE TABLE $TABLE_ASSESSMENTS ($COLUMN_ASSESSMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "$COLUMN_ASSESSMENT_NAME TEXT, $COLUMN_ASSESSMENT_WEIGHT DOUBLE, $COLUMN_ASSESSMENT_COURSE_ID INTEGER, " +
                        "$COLUMN_ASSESSMENT_USER_ID INTEGER, " +
                        "FOREIGN KEY($COLUMN_ASSESSMENT_COURSE_ID) REFERENCES $TABLE_COURSES($COLUMN_COURSE_ID), " +
                        "FOREIGN KEY($COLUMN_ASSESSMENT_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db?.execSQL(createAssessmentsTable)

        val createActivitiesTable = (
                "CREATE TABLE $TABLE_ACTIVITIES ($COLUMN_ACTIVITY_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "$COLUMN_ACTIVITY_NAME TEXT, $COLUMN_ACTIVITY_MAX_SCORE DOUBLE, $COLUMN_ACTIVITY_USER_SCORE DOUBLE, " +
                        "$COLUMN_ACTIVITY_ASSESSMENT_ID INTEGER, $COLUMN_ACTIVITY_USER_ID INTEGER, " +
                        "FOREIGN KEY($COLUMN_ACTIVITY_ASSESSMENT_ID) REFERENCES $TABLE_ASSESSMENTS($COLUMN_ASSESSMENT_ID), " +
                        "FOREIGN KEY($COLUMN_ACTIVITY_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db?.execSQL(createActivitiesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_COURSES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ASSESSMENTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITIES")
        onCreate(db)
    }

    fun addUser(userName: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, userName)
        values.put(COLUMN_USER_PASSWORD, password)

        val id = db.insert(TABLE_USERS, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun checkUserCredentials(context: Context, userName: String, password: String): Boolean {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_USER_ID, COLUMN_USER_NAME)
        val selection =
            "$COLUMN_USER_NAME = ? AND $COLUMN_USER_PASSWORD = ?"
        val selectionArgs = arrayOf(userName, password)

        val cursor: Cursor = db.query(
            TABLE_USERS, columns, selection,
            selectionArgs, null, null, null
        )

        val cursorCount = cursor.count

        if (cursorCount > 0 && cursor.moveToFirst()) {
            val userIdIndex = cursor.getColumnIndex(COLUMN_USER_ID)
            val userNameIndex = cursor.getColumnIndex(COLUMN_USER_NAME)

            if (userIdIndex != -1 && userNameIndex != -1) {
                val userId = cursor.getLong(userIdIndex)
                val userName = cursor.getString(userNameIndex)

                // Save the user ID and user name to SharedPreferences
                saveLoggedInUserInfo(context, userId, userName)
            }
        }

        cursor.close()
        db.close()

        return cursorCount > 0
    }

    // Function to save the user ID to SharedPreferences
    private fun saveLoggedInUserInfo(context: Context, userId: Long, userNameValue: String) {
        val sharedPreferences = context.getSharedPreferences("LoggedInUser", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("userId", userId)
        editor.putString("userName", userNameValue)
        editor.apply()
    }

    fun addCourse(userId: Long, courseName: String, courseDesc: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_ID, userId)
        values.put(COLUMN_COURSE_NAME, courseName)
        values.put(COLUMN_COURSE_DESC, courseDesc)

        val id = db.insert(TABLE_COURSES, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getCoursesFromDatabase(userId: Long): List<Courses> {
        val coursesList = mutableListOf<Courses>()

        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_COURSE_ID, COLUMN_COURSE_NAME, COLUMN_COURSE_DESC)
        val selection = "$COLUMN_COURSE_USER_ID = ?"
        val selectionArgs = arrayOf(userId.toString())

        val cursor = db.query(
            TABLE_COURSES, columns, selection,
            selectionArgs, null, null, null
        )

        while (cursor.moveToNext()) {
            val courseId = cursor.getLong(cursor.getColumnIndex(COLUMN_COURSE_ID))
            val courseName = cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_NAME))
            val courseDesc = cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_DESC))

            val courses = Courses(courseId, courseName, courseDesc)
            coursesList.add(courses)
        }

        cursor.close()
        db.close()

        return coursesList
    }

    fun saveCourseToDatabase(userId: Long, courseName: String, courseDesc: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_ID, userId)
        values.put(COLUMN_COURSE_NAME, courseName)
        values.put(COLUMN_COURSE_DESC, courseDesc)

        val courseId = db.insert(TABLE_COURSES, null, values)
        db.close()

        return courseId
    }

    fun updateCourseInDatabase(courseId: Long, updatedCourseName: String, updatedCourseDesc: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_COURSE_NAME, updatedCourseName)
            put(COLUMN_COURSE_DESC, updatedCourseDesc)
        }

        // Update the course record
        val whereClause = "$COLUMN_COURSE_ID = ?"
        val whereArgs = arrayOf(courseId.toString())

        val rowsUpdated = db.update(TABLE_COURSES, values, whereClause, whereArgs)
        db.close()

        return rowsUpdated
    }


    @SuppressLint("Range")
    fun deleteCourseFromDatabase(courseId: Long, userId: Long) {
        val db = this.writableDatabase

        // Step 2: Check the assessments connected to the course
        val assessmentColumns = arrayOf(COLUMN_ASSESSMENT_ID)
        val assessmentSelection = "$COLUMN_ASSESSMENT_COURSE_ID = ? AND $COLUMN_ASSESSMENT_USER_ID = ?"
        val assessmentSelectionArgs = arrayOf(courseId.toString(), userId.toString())

        val assessmentCursor = db.query(
            TABLE_ASSESSMENTS, assessmentColumns, assessmentSelection,
            assessmentSelectionArgs, null, null, null
        )

        while (assessmentCursor.moveToNext()) {
            // Step 3: Check the activities connected to the assessment
            val assessmentId = assessmentCursor.getLong(assessmentCursor.getColumnIndex(COLUMN_ASSESSMENT_ID))
            val activityWhereClause = "$COLUMN_ACTIVITY_ASSESSMENT_ID = ? AND $COLUMN_ACTIVITY_USER_ID = ?"
            val activityWhereArgs = arrayOf(assessmentId.toString(), userId.toString())

            // Step 4: Function will delete all of the activities
            db.delete(TABLE_ACTIVITIES, activityWhereClause, activityWhereArgs)
        }

        assessmentCursor.close()

        // Step 5: Function will delete all assessments
        val assessmentWhereClause = "$COLUMN_ASSESSMENT_COURSE_ID = ? AND $COLUMN_ASSESSMENT_USER_ID = ?"
        val assessmentWhereArgs = arrayOf(courseId.toString(), userId.toString())
        db.delete(TABLE_ASSESSMENTS, assessmentWhereClause, assessmentWhereArgs)

        // Step 6: Finally, the function will delete the course itself
        val courseWhereClause = "$COLUMN_COURSE_ID = ? AND $COLUMN_COURSE_USER_ID = ?"
        val courseWhereArgs = arrayOf(courseId.toString(), userId.toString())
        db.delete(TABLE_COURSES, courseWhereClause, courseWhereArgs)

        db.close()
    }

    fun saveAssessmentToDatabase(userId: Long, courseId: Long, assessmentName: String, gradeWeight: Double): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ASSESSMENT_NAME, assessmentName)
            put(COLUMN_ASSESSMENT_WEIGHT, gradeWeight)
            put(COLUMN_ASSESSMENT_COURSE_ID, courseId)
            put(COLUMN_ASSESSMENT_USER_ID, userId) // Add user ID
        }

        val assessmentId = db.insert(TABLE_ASSESSMENTS, null, values)
        db.close()

        return assessmentId
    }

    @SuppressLint("Range")
    fun getActivitiesFromDatabase(assessmentId: Int, userId: Long): List<ChildItem> {
        val activitiesList = mutableListOf<ChildItem>()

        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_ACTIVITY_NAME, COLUMN_ACTIVITY_USER_SCORE, COLUMN_ACTIVITY_MAX_SCORE)
        val selection = "$COLUMN_ACTIVITY_ASSESSMENT_ID = ? AND $COLUMN_ACTIVITY_USER_ID = ?"
        val selectionArgs = arrayOf(assessmentId.toString(), userId.toString())

        val cursor = db.query(
            TABLE_ACTIVITIES, columns, selection,
            selectionArgs, null, null, null
        )

        while (cursor.moveToNext()) {
            val activityName = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_NAME))
            val userScore = cursor.getDouble(cursor.getColumnIndex(COLUMN_ACTIVITY_USER_SCORE))
            val maxScore = cursor.getDouble(cursor.getColumnIndex(COLUMN_ACTIVITY_MAX_SCORE))

            val activity = ChildItem(activityName, userScore, maxScore, userId.toInt())
            activitiesList.add(activity)
        }

        cursor.close()
        db.close()

        return activitiesList
    }

    fun saveActivityToDatabase(userId: Long, courseId: Long, activityName: String, userScore: Double, maxScore: Double): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACTIVITY_NAME, activityName)
            put(COLUMN_ACTIVITY_USER_SCORE, userScore)
            put(COLUMN_ACTIVITY_MAX_SCORE, maxScore)
            put(COLUMN_ACTIVITY_ASSESSMENT_ID, courseId) // Assuming the assessment_id is the same as courseId
            put(COLUMN_ACTIVITY_USER_ID, userId)
        }

        val activityId = db.insert(TABLE_ACTIVITIES, null, values)
        db.close()

        return activityId
    }

    @SuppressLint("Range")
    fun fetchParentList(userId: Long): ArrayList<ParentItem> {
        // Retrieve parent items (Assessments) from the database
        val columns = arrayOf(COLUMN_ASSESSMENT_ID, COLUMN_ASSESSMENT_NAME, COLUMN_ASSESSMENT_WEIGHT)
        val selection = "$COLUMN_ASSESSMENT_USER_ID = ?"
        val selectionArgs = arrayOf(userId.toString())

        val cursor = readableDatabase.query(
            TABLE_ASSESSMENTS, columns, selection,
            selectionArgs, null, null, null
        )

        val parentList = ArrayList<ParentItem>()

        while (cursor.moveToNext()) {
            val assessmentId = cursor.getInt(cursor.getColumnIndex(COLUMN_ASSESSMENT_ID))
            val assessmentName = cursor.getString(cursor.getColumnIndex(COLUMN_ASSESSMENT_NAME))
            val gradeWeight = cursor.getDouble(cursor.getColumnIndex(COLUMN_ASSESSMENT_WEIGHT))

            val parentItem = ParentItem(assessmentId, assessmentName, gradeWeight, userId.toInt())
            parentList.add(parentItem)
        }

        cursor.close()

        return parentList
    }
}