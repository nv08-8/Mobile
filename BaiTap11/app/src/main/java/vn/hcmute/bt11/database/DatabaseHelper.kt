package vn.hcmute.bt11.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import vn.hcmute.bt11.models.Task
import vn.hcmute.bt11.models.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TodoListDB"
        private const val DATABASE_VERSION = 1

        // User Table
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"

        // Task Table
        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_TASK_ID = "task_id"
        private const val COLUMN_TASK_USER_ID = "user_id"
        private const val COLUMN_TASK_TITLE = "title"
        private const val COLUMN_TASK_DESCRIPTION = "description"
        private const val COLUMN_TASK_DATE = "date"
        private const val COLUMN_TASK_TIME = "time"
        private const val COLUMN_TASK_COMPLETED = "completed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Users Table
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        // Create Tasks Table
        val createTasksTable = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TASK_USER_ID INTEGER NOT NULL,
                $COLUMN_TASK_TITLE TEXT NOT NULL,
                $COLUMN_TASK_DESCRIPTION TEXT,
                $COLUMN_TASK_DATE TEXT,
                $COLUMN_TASK_TIME TEXT,
                $COLUMN_TASK_COMPLETED INTEGER DEFAULT 0,
                FOREIGN KEY($COLUMN_TASK_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()

        db?.execSQL(createUsersTable)
        db?.execSQL(createTasksTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // User Operations
    fun registerUser(username: String, email: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun loginUser(username: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_EMAIL),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            )
        }
        cursor.close()
        return user
    }

    fun isUsernameExists(username: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun checkUsernameExists(username: String): Boolean {
        return isUsernameExists(username)
    }

    fun checkEmailExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Task Operations
    fun addTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TASK_USER_ID, task.userId)
            put(COLUMN_TASK_TITLE, task.title)
            put(COLUMN_TASK_DESCRIPTION, task.description)
            put(COLUMN_TASK_DATE, task.date)
            put(COLUMN_TASK_TIME, task.time)
            put(COLUMN_TASK_COMPLETED, if (task.isCompleted) 1 else 0)
        }
        return db.insert(TABLE_TASKS, null, values)
    }

    fun updateTask(task: Task): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TASK_TITLE, task.title)
            put(COLUMN_TASK_DESCRIPTION, task.description)
            put(COLUMN_TASK_DATE, task.date)
            put(COLUMN_TASK_TIME, task.time)
            put(COLUMN_TASK_COMPLETED, if (task.isCompleted) 1 else 0)
        }
        return db.update(TABLE_TASKS, values, "$COLUMN_TASK_ID = ?", arrayOf(task.id.toString()))
    }

    fun deleteTask(taskId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_TASKS, "$COLUMN_TASK_ID = ?", arrayOf(taskId.toString()))
    }

    fun getAllTasksForUser(userId: Int): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TASKS,
            null,
            "$COLUMN_TASK_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null,
            "$COLUMN_TASK_DATE DESC, $COLUMN_TASK_TIME DESC"
        )

        while (cursor.moveToNext()) {
            tasks.add(
                Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_USER_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DESCRIPTION)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DATE)),
                    time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TIME)),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_COMPLETED)) == 1
                )
            )
        }
        cursor.close()
        return tasks
    }

    fun toggleTaskCompletion(taskId: Int): Boolean {
        val db = writableDatabase
        val cursor = db.query(
            TABLE_TASKS,
            arrayOf(COLUMN_TASK_COMPLETED),
            "$COLUMN_TASK_ID = ?",
            arrayOf(taskId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val currentStatus = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_COMPLETED))
            val newStatus = if (currentStatus == 0) 1 else 0

            val values = ContentValues().apply {
                put(COLUMN_TASK_COMPLETED, newStatus)
            }
            cursor.close()
            db.update(TABLE_TASKS, values, "$COLUMN_TASK_ID = ?", arrayOf(taskId.toString()))
            return newStatus == 1
        }
        cursor.close()
        return false
    }
}

