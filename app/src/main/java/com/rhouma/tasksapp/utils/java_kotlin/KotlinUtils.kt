package com.rhouma.tasksapp.utils.java_kotlin


fun String?.isTaskCompleted(): Boolean {
    return this?.startsWith("Done") == true
}

// Usage of this extension function
/**
 * val taskName = "Done Task"
 * val isCompleted = taskName.isTaskCompleted() // Returns true
 */

