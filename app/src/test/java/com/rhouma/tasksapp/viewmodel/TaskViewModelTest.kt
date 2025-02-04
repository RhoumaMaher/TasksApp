package com.rhouma.tasksapp.viewmodel

import com.rhouma.tasksapp.data.common.BaseResult
import com.rhouma.tasksapp.data.local.TaskEntity
import com.rhouma.tasksapp.data.remote.TaskModel
import com.rhouma.tasksapp.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private lateinit var viewModel: TaskViewModel
    private lateinit var repository: TaskRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = TaskViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchRemoteTasks should update tasks with Success when API call is successful`() =
        runTest {
            val taskModels = listOf(TaskModel(1, "Task 1", false))
            val taskEntities = taskModels.map { TaskEntity(it.id, it.title, it.completed) }
            val successResult = BaseResult.Success(taskEntities)

            `when`(repository.getRemoteTasks()).thenReturn(
                flowOf(
                    BaseResult.Loading,
                    successResult
                )
            )

            viewModel.fetchRemoteTasks()

            assertEquals(successResult, viewModel.tasks.value)
        }

    @Test
    fun `fetchRemoteTasks should fallback to fetchLocalTasks when API call fails`() = runTest {
        val errorResult = BaseResult.Error("API call failed")
        val localTasks = listOf(TaskEntity(1, "Task 1", false))
        val localSuccessResult = BaseResult.Success(localTasks)

        `when`(repository.getRemoteTasks()).thenReturn(flowOf(BaseResult.Loading, errorResult))
        `when`(repository.getLocalTasks()).thenReturn(
            flowOf(
                BaseResult.Loading,
                localSuccessResult
            )
        )

        viewModel.fetchRemoteTasks()

        assertEquals(localSuccessResult, viewModel.tasks.value)
    }

    @Test
    fun `fetchRemoteTasks should update tasks with NoInternet when there is no internet`() =
        runTest {
            `when`(repository.getRemoteTasks()).thenReturn(
                flowOf(
                    BaseResult.Loading,
                    BaseResult.NoInternet
                )
            )

            viewModel.fetchRemoteTasks()

            assertEquals(BaseResult.NoInternet, viewModel.tasks.value)
        }

    @Test
    fun `fetchLocalTasks should update tasks with Success when local data is retrieved`() =
        runTest {
            val localTasks = listOf(TaskEntity(1, "Task 1", false))
            val localSuccessResult = BaseResult.Success(localTasks)

            `when`(repository.getLocalTasks()).thenReturn(
                flowOf(
                    BaseResult.Loading,
                    localSuccessResult
                )
            )

            viewModel.fetchLocalTasks()

            assertEquals(localSuccessResult, viewModel.tasks.value)
        }

    @Test
    fun `fetchLocalTasks should update tasks with Error when local data retrieval fails`() =
        runTest {
            val errorResult = BaseResult.Error("Database error")

            `when`(repository.getLocalTasks()).thenReturn(flowOf(BaseResult.Loading, errorResult))

            viewModel.fetchLocalTasks()

            assertEquals(errorResult, viewModel.tasks.value)
        }

}