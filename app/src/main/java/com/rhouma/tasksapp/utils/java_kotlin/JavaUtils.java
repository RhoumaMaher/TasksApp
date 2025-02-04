package com.rhouma.tasksapp.utils.java_kotlin;

public class JavaUtils {
    public static boolean isTaskCompleted(String taskName) {
        return taskName != null && taskName.startsWith("Done");
    }
}
