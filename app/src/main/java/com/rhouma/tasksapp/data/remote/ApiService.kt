package com.rhouma.tasksapp.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("tasks")
    suspend fun getTasks(): Response<List<TaskModel>>

    @POST("tasks")
    suspend fun addTask(@Body task: TaskModel): Response<Boolean>
}
