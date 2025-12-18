package vn.hcmute.bt10_kotlin

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import vn.hcmute.bt10_kotlin.adapters.TaskAdapter
import vn.hcmute.bt10_kotlin.database.DatabaseHelper
import vn.hcmute.bt10_kotlin.databinding.ActivityMainBinding
import vn.hcmute.bt10_kotlin.databinding.DialogAddTaskBinding
import vn.hcmute.bt10_kotlin.models.Task
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private var userId: Int = -1
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        databaseHelper = DatabaseHelper(this)

        // Get user info from SharedPreferences
        val sharedPref = getSharedPreferences("TodoListPrefs", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("userId", -1)
        username = sharedPref.getString("username", "") ?: ""

        if (userId == -1) {
            // User not logged in, redirect to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding.tvWelcome.text = "Xin chào, $username!"

        setupRecyclerView()
        loadTasks()

        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            tasks = mutableListOf(),
            onEditClick = { task -> showEditTaskDialog(task) },
            onDeleteClick = { task -> confirmDeleteTask(task) },
            onCheckChange = { task -> toggleTaskCompletion(task) }
        )
        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }

    private fun loadTasks() {
        val tasks = databaseHelper.getAllTasksForUser(userId)
        taskAdapter.updateTasks(tasks)
        updateEmptyState(tasks.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.rvTasks.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvTasks.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }

    private fun showAddTaskDialog(taskToEdit: Task? = null) {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        // If editing, populate fields
        taskToEdit?.let { task ->
            dialogBinding.etTitle.setText(task.title)
            dialogBinding.etDescription.setText(task.description)
            dialogBinding.etDate.setText(task.date)
            dialogBinding.etTime.setText(task.time)
        }

        // Date picker
        dialogBinding.etDate.setOnClickListener {
            showDatePicker { date ->
                dialogBinding.etDate.setText(date)
            }
        }

        // Time picker
        dialogBinding.etTime.setOnClickListener {
            showTimePicker { time ->
                dialogBinding.etTime.setText(time)
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSave.setOnClickListener {
            val title = dialogBinding.etTitle.text.toString().trim()
            val description = dialogBinding.etDescription.text.toString().trim()
            val date = dialogBinding.etDate.text.toString().trim()
            val time = dialogBinding.etTime.text.toString().trim()

            if (title.isEmpty()) {
                dialogBinding.etTitle.error = "Vui lòng nhập tiêu đề"
                return@setOnClickListener
            }

            if (taskToEdit == null) {
                // Add new task
                val task = Task(
                    userId = userId,
                    title = title,
                    description = description.ifEmpty { null },
                    date = date.ifEmpty { null },
                    time = time.ifEmpty { null }
                )
                val result = databaseHelper.addTask(task)
                if (result != -1L) {
                    Toast.makeText(this, "Đã thêm công việc", Toast.LENGTH_SHORT).show()
                    loadTasks()
                    dialog.dismiss()
                }
            } else {
                // Update existing task
                val updatedTask = taskToEdit.copy(
                    title = title,
                    description = description.ifEmpty { null },
                    date = date.ifEmpty { null },
                    time = time.ifEmpty { null }
                )
                val result = databaseHelper.updateTask(updatedTask)
                if (result > 0) {
                    Toast.makeText(this, "Đã cập nhật công việc", Toast.LENGTH_SHORT).show()
                    loadTasks()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun showEditTaskDialog(task: Task) {
        showAddTaskDialog(task)
    }

    private fun confirmDeleteTask(task: Task) {
        AlertDialog.Builder(this)
            .setTitle("Xóa công việc")
            .setMessage("Bạn có chắc chắn muốn xóa công việc này?")
            .setPositiveButton("Xóa") { _, _ ->
                val result = databaseHelper.deleteTask(task.id)
                if (result > 0) {
                    Toast.makeText(this, "Đã xóa công việc", Toast.LENGTH_SHORT).show()
                    loadTasks()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun toggleTaskCompletion(task: Task) {
        databaseHelper.toggleTaskCompletion(task.id)
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = Calendar.getInstance()
            date.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            onDateSelected(dateFormat.format(date.time))
        }, year, month, day).show()
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(time)
        }, hour, minute, true).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                val sharedPref = getSharedPreferences("TodoListPrefs", Context.MODE_PRIVATE)
                sharedPref.edit().clear().apply()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}