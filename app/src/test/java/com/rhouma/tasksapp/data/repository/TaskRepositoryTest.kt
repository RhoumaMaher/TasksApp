package com.rhouma.tasksapp.data.repository

import com.rhouma.tasksapp.data.common.BaseResult
import com.rhouma.tasksapp.data.local.TaskDao
import com.rhouma.tasksapp.data.local.TaskEntity
import com.rhouma.tasksapp.data.remote.ApiService
import com.rhouma.tasksapp.data.remote.TaskModel
import com.rhouma.tasksapp.utils.NetworkHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import retrofit2.Response

class TaskRepositoryTest {

    private lateinit var repository: TaskRepository
    private lateinit var apiService: ApiService
    private lateinit var taskDao: TaskDao
    private lateinit var networkHelper: NetworkHelper

    @Before
    fun setUp() {
        apiService = mock()
        taskDao = mock()
        networkHelper = mock()
        repository = TaskRepository(apiService, taskDao, networkHelper)
    }

    @Test
    fun `getRemoteTasks should emit Loading and then Success when network is available and API call is successful`() = runTest {
        val taskModels = listOf(TaskModel(1, "Task 1", false))
        val taskEntities = taskModels.map { TaskEntity(it.id, it.title, it.completed) }
        val response = Response.success(taskModels)

        `when`(networkHelper.isInternetAvailable()).thenReturn(true)
        `when`(apiService.getTasks()).thenReturn(response)

        val results = mutableListOf<BaseResult<List<TaskEntity>, String>>()
        repository.getRemoteTasks().collect { result ->
            results.add(result)
        }

        assertEquals(2, results.size) // Ensure there are exactly 2 emissions
        assert(results[0] is BaseResult.Loading) // First emission is Loading
        assert(results[1] is BaseResult.Success) // Second emission is Success
        assertEquals(taskEntities, (results[1] as BaseResult.Success).data) // Verify the data
    }

    @Test
    fun `getRemoteTasks should emit NoInternet when network is not available`() = runTest {
        `when`(networkHelper.isInternetAvailable()).thenReturn(false) // Mock no internet

        val results = repository.getRemoteTasks().toList()

        // Debugging: Print the results to see what's being emitted
        println("Results: $results")

        assertEquals(2, results.size)
        assert(results[0] is BaseResult.Loading)
        assert(results[1] is BaseResult.NoInternet)
    }

    @Test
    fun `getRemoteTasks should emit Error when API call fails`() = runTest {
        `when`(networkHelper.isInternetAvailable()).thenReturn(true) // Mock internet available
        `when`(apiService.getTasks()).thenThrow(RuntimeException("API call failed")) // Mock API failure

        val results = repository.getRemoteTasks().toList()

        assertEquals(2, results.size)
        assert(results[0] is BaseResult.Loading)
        assert(results[1] is BaseResult.Error)
    }

    @Test
    fun `getLocalTasks should emit Loading and then Success when tasks are retrieved from local database`() = runTest {
        val taskEntities = listOf(TaskEntity(1, "Task 1", false))
        `when`(taskDao.getTasks()).thenReturn(taskEntities) // Mock local data

        val results = repository.getLocalTasks().toList()


        assertEquals(2, results.size)
        assert(results[0] is BaseResult.Loading)
        assert(results[1] is BaseResult.Success)
        assertEquals(taskEntities, (results[1] as BaseResult.Success).data)
    }

    @Test
    fun `getLocalTasks should emit Error when an exception occurs`() = runTest {
        `when`(taskDao.getTasks()).thenThrow(RuntimeException("Database error")) // Mock database failure

        val results = repository.getLocalTasks().toList()

        assertEquals(2, results.size)
        assert(results[0] is BaseResult.Loading)
        assert(results[1] is BaseResult.Error)
    }

    @Test
    fun `addLocalTask should insert task into local database`() = runTest {
        val task = TaskEntity(1, "Task 1", false)

        repository.addLocalTask(task)

        verify(taskDao).insert(task)
    }

    @Test
    fun `addRemoteTask should call API to add task`() = runTest {
        val task = TaskModel(1, "Task 1", false)

        repository.addRemoteTask(task)

        verify(apiService).addTask(task)
    }

    @Test
    fun `updateTaskStatus should update task status in local database`() = runTest {
        val id = 1
        val isCompleted = true

        repository.updateTaskStatus(id, isCompleted)

        verify(taskDao).updateTaskStatus(id, isCompleted)
    }
}