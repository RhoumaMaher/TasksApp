package com.rhouma.tasksapp.data.repository

import com.rhouma.tasksapp.data.common.BaseResult
import com.rhouma.tasksapp.data.local.TaskDao
import com.rhouma.tasksapp.data.local.TaskEntity
import com.rhouma.tasksapp.data.remote.ApiService
import com.rhouma.tasksapp.data.remote.TaskModel
import com.rhouma.tasksapp.utils.NetworkHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskRepository(
    private val api: ApiService,
    private val taskDao: TaskDao,
    private val networkHelper: NetworkHelper
) {

    suspend fun getRemoteTasks(): Flow<BaseResult<List<TaskEntity>, String>> = flow {
        emit(BaseResult.Loading)
        try {
            if (networkHelper.isInternetAvailable()) {
                val response = api.getTasks()
                if (response.isSuccessful) {
                    val data = response.body()
                    val tasksList = mutableListOf<TaskEntity>()
                    data?.let { tasks ->
                        tasks.map { taskModel ->
                            tasksList.add(
                                TaskEntity(
                                    id = taskModel.id,
                                    name = taskModel.title,
                                    isCompleted = taskModel.completed
                                )
                            )
                        }
                        // Sync local database with remote tasks
                        taskDao.insertAll(tasksList)
                        // Emit the result
                        emit(BaseResult.Success(tasksList))
                    }
                } else {
                    emit(BaseResult.Error("An error occurred"))
                }
            } else {
                // No internet is available
                emit(BaseResult.NoInternet)
            }
        } catch (e: Exception) {
            emit(BaseResult.Error("An error occurred"))
        }
    }

    suspend fun getLocalTasks(): Flow<BaseResult<List<TaskEntity>, String>> = flow {
        emit(BaseResult.Loading)
        try {
            val tasks = taskDao.getTasks()
            emit(BaseResult.Success(tasks))
        } catch (e: Exception) {
            emit(BaseResult.Error("An error occurred"))
        }
    }

    suspend fun addLocalTask(task: TaskEntity) {
        try {
            taskDao.insert(task)
        } catch (e: Exception) {
            // Handle failure
            e.printStackTrace()
        }
    }

    suspend fun addRemoteTask(task: TaskModel) {
        try {
            api.addTask(TaskModel(task.id, task.title, task.completed))
        } catch (e: Exception) {
            // Handle failure
            e.printStackTrace()
        }
    }

    suspend fun updateTaskStatus(id: Int, isCompleted: Boolean) {
        taskDao.updateTaskStatus(id, isCompleted)
    }
}
