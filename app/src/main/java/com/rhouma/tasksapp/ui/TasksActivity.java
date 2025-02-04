package com.rhouma.tasksapp.ui;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.compose.ui.platform.ComposeView;
import androidx.lifecycle.ViewModelProvider;
import com.rhouma.tasksapp.R;
import com.rhouma.tasksapp.data.local.AppDatabase;
import com.rhouma.tasksapp.data.remote.ApiService;
import com.rhouma.tasksapp.data.repository.TaskRepository;
import com.rhouma.tasksapp.ui.screens.TaskScreenKt;
import com.rhouma.tasksapp.utils.NetworkHelper;
import com.rhouma.tasksapp.utils.UiUtilsKt;
import com.rhouma.tasksapp.viewmodel.TaskViewModel;
import com.rhouma.tasksapp.viewmodel.TaskViewModelFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// This is the Java version
public class TasksActivity extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        ApiService apiService = new Retrofit.Builder()
                .baseUrl("https://rhouma.com/api/") // Since we don't have an actual API, we'll use a fictive url
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);

        // Initialize the Local Database
        AppDatabase database = AppDatabase.Companion.getInstance(getApplicationContext());

        // Initialize the Network Helper
        NetworkHelper networkHelper = new NetworkHelper(getApplicationContext());

        // Initialize our repository
        TaskRepository repository = new TaskRepository(apiService, database.taskDao(), networkHelper);

        // Create ViewModelProvider.Factory
        TaskViewModelFactory factory = new TaskViewModelFactory(repository);

        // Initialize ViewModel with the custom factory we made
        TaskViewModel viewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);

        // Find the ComposeView in our layout
        ComposeView composeView = findViewById(R.id.my_compose_view);

        // Set the Composable content
        UiUtilsKt.setComposeContent(composeView, viewModel);

    }

}