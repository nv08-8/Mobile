package vn.hcmute.bt11.repository

import vn.hcmute.bt11.database.DatabaseHelper
import vn.hcmute.bt11.models.Task

class TaskRepository(private val databaseHelper: DatabaseHelper) {

    fun getAllTasksForUser(userId: Int): List<Task> {
        return databaseHelper.getAllTasksForUser(userId)
    }

    fun addTask(task: Task): Long {
        return databaseHelper.addTask(task)
    }

    fun updateTask(task: Task): Int {
        return databaseHelper.updateTask(task)
    }

    fun deleteTask(taskId: Int): Int {
        return databaseHelper.deleteTask(taskId)
    }

    fun toggleTaskCompletion(taskId: Int) {
        databaseHelper.toggleTaskCompletion(taskId)
    }
}

