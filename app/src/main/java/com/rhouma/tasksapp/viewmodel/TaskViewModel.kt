package com.rhouma.tasksapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhouma.tasksapp.data.common.BaseResult
import com.rhouma.tasksapp.data.local.TaskEntity
import com.rhouma.tasksapp.data.remote.TaskModel
import com.rhouma.tasksapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<BaseResult<List<TaskEntity>, String>>(BaseResult.Loading)
    val tasks: StateFlow<BaseResult<List<TaskEntity>, String>> = _tasks

    init {
        fetchRemoteTasks()
    }

    fun fetchRemoteTasks() {
        viewModelScope.launch {
            repository.getRemoteTasks().collect { result ->
                when (result) {
                    is BaseResult.Success -> {
                        _tasks.value = result
                    }

                    is BaseResult.Error -> {
                        fetchLocalTasks()
                    }

                    BaseResult.Init -> {
                        // Handle init state
                    }

                    BaseResult.Loading -> {
                        // Handle loading state
                    }

                    BaseResult.NoInternet -> {
                        _tasks.value = BaseResult.NoInternet
                    }
                }
            }
        }
    }

    fun fetchLocalTasks() {
        viewModelScope.launch {
            repository.getLocalTasks().collect { result ->
                _tasks.value = result
            }
        }
    }

    fun addTask(task: String) {
        viewModelScope.launch {
            repository.addLocalTask(TaskEntity(name = task, isCompleted = false))
            // sync with remote
            repository.addRemoteTask(TaskModel(id = 0, title = task, completed = false))
            fetchLocalTasks()
        }
    }

    fun toggleTaskStatus(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTaskStatus(task.id, !task.isCompleted)
            fetchLocalTasks()
        }
    }
}
