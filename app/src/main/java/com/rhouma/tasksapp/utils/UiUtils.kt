package com.rhouma.tasksapp.utils

import androidx.compose.ui.platform.ComposeView
import com.rhouma.tasksapp.ui.screens.TaskScreen
import com.rhouma.tasksapp.viewmodel.TaskViewModel

fun setComposeContent(composeView: ComposeView, viewModel: TaskViewModel) {
    composeView.setContent {
        TaskScreen(viewModel)
    }
}