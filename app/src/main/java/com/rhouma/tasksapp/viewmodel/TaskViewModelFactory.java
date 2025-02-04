package com.rhouma.tasksapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rhouma.tasksapp.data.repository.TaskRepository;

public class TaskViewModelFactory implements ViewModelProvider.Factory {
    private final TaskRepository repository;

    public TaskViewModelFactory(TaskRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new TaskViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}