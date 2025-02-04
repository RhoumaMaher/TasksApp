package com.rhouma.tasksapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rhouma.tasksapp.data.local.AppDatabase
import com.rhouma.tasksapp.data.remote.ApiService
import com.rhouma.tasksapp.data.repository.TaskRepository
import com.rhouma.tasksapp.ui.screens.TaskScreen
import com.rhouma.tasksapp.ui.theme.TasksAppTheme
import com.rhouma.tasksapp.utils.NetworkHelper
import com.rhouma.tasksapp.viewmodel.TaskViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// This is the kotlin version
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService = Retrofit.Builder()
            .baseUrl("https://example.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val database = AppDatabase.getInstance(applicationContext)
        val networkHelper = NetworkHelper(applicationContext)
        val repository = TaskRepository(apiService, database.taskDao(), networkHelper)
        val viewModel = TaskViewModel(repository)

        setContent {
            TaskScreen(viewModel = viewModel)
        }
    }
}
