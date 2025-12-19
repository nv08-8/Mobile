package vn.hcmute.bt11.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.hcmute.bt11.models.Task
import vn.hcmute.bt11.repository.TaskRepository

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _isEmptyState = MutableLiveData<Boolean>()
    val isEmptyState: LiveData<Boolean> = _isEmptyState

    private val _operationResult = MutableLiveData<OperationResult>()
    val operationResult: LiveData<OperationResult> = _operationResult

    fun loadTasks(userId: Int) {
        val taskList = taskRepository.getAllTasksForUser(userId)
        _tasks.value = taskList
        _isEmptyState.value = taskList.isEmpty()
    }

    fun addTask(task: Task) {
        val result = taskRepository.addTask(task)
        if (result != -1L) {
            _operationResult.value = OperationResult.Success("Đã thêm công việc")
            loadTasks(task.userId)
        } else {
            _operationResult.value = OperationResult.Error("Thêm công việc thất bại")
        }
    }

    fun updateTask(task: Task) {
        val result = taskRepository.updateTask(task)
        if (result > 0) {
            _operationResult.value = OperationResult.Success("Đã cập nhật công việc")
            loadTasks(task.userId)
        } else {
            _operationResult.value = OperationResult.Error("Cập nhật thất bại")
        }
    }

    fun deleteTask(task: Task) {
        val result = taskRepository.deleteTask(task.id)
        if (result > 0) {
            _operationResult.value = OperationResult.Success("Đã xóa công việc")
            loadTasks(task.userId)
        } else {
            _operationResult.value = OperationResult.Error("Xóa thất bại")
        }
    }

    fun toggleTaskCompletion(task: Task) {
        taskRepository.toggleTaskCompletion(task.id)
        loadTasks(task.userId)
    }

    sealed class OperationResult {
        data class Success(val message: String) : OperationResult()
        data class Error(val message: String) : OperationResult()
    }
}

