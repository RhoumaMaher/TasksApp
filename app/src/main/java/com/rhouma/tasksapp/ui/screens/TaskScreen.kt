package com.rhouma.tasksapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rhouma.tasksapp.data.common.BaseResult
import com.rhouma.tasksapp.data.local.TaskEntity
import com.rhouma.tasksapp.viewmodel.TaskViewModel

@Composable
fun TaskScreen(viewModel: TaskViewModel) {

    val context = LocalContext.current
    val tasksState by viewModel.tasks.collectAsState()


    LaunchedEffect(tasksState) {
        when (tasksState) {
            is BaseResult.Error -> {
                val errorMessage = (tasksState as BaseResult.Error<String>).rawResponse
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

            BaseResult.NoInternet -> {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                // Fetch local tasks in case of internet failure
                viewModel.fetchLocalTasks()
            }

            else -> Unit
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var taskName by remember { mutableStateOf("") }

            if (tasksState is BaseResult.Loading) {
                CircularProgressIndicator()
            }

            if (tasksState is BaseResult.Success) {
                val tasks = (tasksState as BaseResult.Success<List<TaskEntity>>).data
                LazyColumn {
                    items(tasks) { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = task.name,
                                fontSize = 22.sp,
                                modifier = Modifier.weight(1f),
                                style = if (task.isCompleted) TextStyle(textDecoration = TextDecoration.LineThrough)
                                else TextStyle()
                            )
                            Switch(checked = task.isCompleted, onCheckedChange = {
                                viewModel.toggleTaskStatus(task)
                            })
                        }
                    }
                }

                TextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Nouvelle tâche") }
                )

                Button(onClick = {
                    if (taskName.isNotBlank()) {
                        viewModel.addTask(taskName)
                        taskName = ""
                    }
                }) {
                    Text("Ajouter")
                }

            }

        }
    }

}

